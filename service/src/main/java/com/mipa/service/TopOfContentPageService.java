package com.mipa.service;


import com.mipa.common.Constant.ExMsg;
import com.mipa.common.Enum.PagePosKind;
import com.mipa.common.Enum.WriterPageResponseType;
import com.mipa.common.Enum.WriterWSOp;
import com.mipa.common.configuration.MyConfiguration;
import com.mipa.common.dto.writerwsdto.ServerCommand;
import com.mipa.common.dto.writerwsdto.ServerCommandSet;
import com.mipa.common.dto.writerwsdto.WriterCommand;
import com.mipa.common.dto.writerwsdto.WriterCommandSet;
import com.mipa.common.exception.BizException;
import com.mipa.common.utils.ChapterContentInfo;
import com.mipa.common.utils.PageCodeAndStartPos;
import com.mipa.common.utils.StringUtils;
import com.mipa.mapper.ChapterContentPageMapper;
import com.mipa.mapper.ChapterMapper;
import com.mipa.model.Chapter;
import com.mipa.model.ChapterContentPage;
import com.mipa.service.api.ITopOfContentPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class TopOfContentPageService implements ITopOfContentPageService {

	@Autowired
	ContentPageService pageService;

	@Autowired
	ChapterContentPageMapper pageMapper;

	@Autowired
	ChapterMapper chapterMapper;

	@Autowired
	MyConfiguration config;

	@Autowired
	ContentPageCacheService pageCacheService;

	public ServerCommandSet scheduleOp(WriterCommandSet commands, String userId, String chapterId, Boolean tryCache) {
		var serverCommands = ServerCommandSet.builder().commands(new ArrayList<ServerCommand>()).build();
		if(tryCache)
			pageCacheService.cacheOrExecute(commands, userId, chapterId);
		if(commands.getCommands().isEmpty())return serverCommands;
		var chapter = pageCacheService.getChapter(chapterId);
		for (var command : commands.getCommands()) {
			switch (command.getType()) {
				case GetPageInfo -> {
					serverCommands.setType(WriterPageResponseType.GetPageInfoResponse);
					pageService.getInfoOp(userId, chapter, command, serverCommands);
				}
				case GetPage -> {
					serverCommands.setType(WriterPageResponseType.GetPageResponse);
					pageService.getOp(userId, chapter, command, serverCommands);
				}
				case UpdatePage_Add -> {
					var childCommands = updateAddOp(userId, chapter, command, serverCommands);
					pageService.scheduleOp(childCommands, userId, chapterId);
				}
				case UpdatePage_Remove -> {
					var childCommands = updateRemoveOp(userId, chapter, command, serverCommands);
					pageService.scheduleOp(childCommands, userId, chapterId);
				}
				case UpdatePage_Replace -> {
					var childCommands =  updateReplaceOp(userId, chapter, command, serverCommands);
					pageService.scheduleOp(childCommands, userId, chapterId);
				}
				default -> {
					break;
				}
			}
		}
		return serverCommands;
	}

	@Override
	public void getOp(String userId, Chapter chapter, WriterCommand command, ServerCommandSet serverCommands) {
		pageService.getOp(userId, chapter, command, serverCommands);
	}

	@Override
	public void getInfoOp(String userId, Chapter chapter, WriterCommand command, ServerCommandSet serverCommands) {
		//serverCommands.getCommands().add(ServerCommand.builder().contentInfo(chapter.getContentInfo()).build());
		pageService.getInfoOp(userId, chapter, command, serverCommands);
	}


	private WriterCommandSet updateAddOp(String userId, Chapter chapter, WriterCommand command, ServerCommandSet serverCommands) {
		var pageCodeAndStartPos = getPageCodeAndStartPos(command.getOtherPos(), chapter.getContentInfo());

		var page = pageService.getPage(chapter.getContentInfo().getPageIds().get(pageCodeAndStartPos.getPageCode()));
		WriterCommandSet commands = WriterCommandSet.builder().commands(new ArrayList<WriterCommand>()).build();
		StringBuilder builder = new StringBuilder(page.getData());
		builder.insert(pageCodeAndStartPos.getStartPos(), command.getData());
		if (builder.length() > config.contentPageTopScale * config.contentPageSize) {
			var datas = StringUtils.splitBySize(builder.toString(), config.contentPageSize);
			commands.getCommands().add(WriterCommand.builder()
					.type(WriterWSOp.UpdatePage_Replace)
					.startPos(pageCodeAndStartPos.getPageCode())
					.otherPos(0).num(-1).data(datas.get(0)).build());
			commands.getCommands().addAll(IntStream.range(1, datas.size()).mapToObj(index->
					WriterCommand.builder().type(WriterWSOp.InsertPage).startPos(pageCodeAndStartPos.getPageCode() + index).data(datas.get(index)).build()).toList());
		}else {
			commands.getCommands().add(
					WriterCommand.builder().type(WriterWSOp.UpdatePage_Add)
							.startPos(pageCodeAndStartPos.getPageCode())
							.otherPos(pageCodeAndStartPos.getStartPos())
							.data(command.getData()).build()
			);
		}
		return commands;
	}

	private WriterCommandSet updateRemoveOp(String userId, Chapter chapter , WriterCommand command, ServerCommandSet serverCommands){

		var pageCodeAndStartPoss = getPageCodeAndStartPos(command.getOtherPos(), command.getNum(), chapter.getContentInfo());

		var start = pageCodeAndStartPoss.get(0);
		var end = pageCodeAndStartPoss.get(1);

		WriterCommandSet commands = WriterCommandSet.builder().commands(new ArrayList<WriterCommand>()).build();

		if(start.getPageCode() == end.getPageCode()){
			commands.getCommands().add(
					WriterCommand.builder().type(WriterWSOp.UpdatePage_Remove).
							startPos(start.getPageCode())
							.otherPos(start.getStartPos()).num(end.getStartPos() - start.getStartPos()).build()
			);
			return commands;
		}

		commands.getCommands().add(
				WriterCommand.builder().type(WriterWSOp.UpdatePage_Remove).
						startPos(start.getPageCode())
						.otherPos(start.getStartPos()).num(-1).build()
		);

		commands.getCommands().add(
				WriterCommand.builder().type(WriterWSOp.UpdatePage_Remove).
						startPos(end.getPageCode())
						.otherPos(0).num(end.getStartPos()).build()
		);

		commands.getCommands().add(
				WriterCommand.builder().type(WriterWSOp.MergePage)
						.startPos(start.getPageCode()).otherPos(end.getPageCode())
						.build()
		);
		commands.getCommands().add(
				WriterCommand.builder().type(WriterWSOp.DeletePage)
						.startPos(start.getPageCode() + 1)
						.num(end.getPageCode() - start.getPageCode() - 1).build());

		return commands;
	}

	private WriterCommandSet updateReplaceOp(String userId, Chapter chapter, WriterCommand command, ServerCommandSet serverCommands) {
		WriterCommandSet commands = WriterCommandSet.builder().commands(new ArrayList<WriterCommand>()).build();
		commands.getCommands().addAll(
				updateRemoveOp(userId, chapter,
						WriterCommand.builder().type(WriterWSOp.UpdatePage_Remove)
								.startPos(command.getStartPos()).otherPos(command.getOtherPos())
								.num(command.getNum()).build(), serverCommands).getCommands()
		);
		commands.getCommands().addAll(
				updateAddOp(userId, chapter,
						WriterCommand.builder().type(WriterWSOp.UpdatePage_Add)
								.startPos(command.getStartPos()).otherPos(command.getOtherPos())
								.data(command.getData()).build(), serverCommands).getCommands()
		);

		return commands;
	}

	private PageCodeAndStartPos getPageCodeAndStartPos(Integer startPos, ChapterContentInfo contentInfo){
		var pageWordCounts = contentInfo.getPageWordCounts();
		Integer curTotal = 0;
		for (int i = 0; i < pageWordCounts.size(); i++) {
			if (startPos == 0 || (curTotal < startPos && curTotal + pageWordCounts.get(i) >= startPos)) {
				return PageCodeAndStartPos.builder().pageCode(i).startPos(startPos - curTotal)
						.pageWordCount(pageWordCounts.get(i)).build();
			}
			curTotal += pageWordCounts.get(i);
		}
		throw BizException.badRequest(ExMsg.PARAM_ERROR_FOR_CONTENT);
	}

	private List<PageCodeAndStartPos> getPageCodeAndStartPos(Integer startPos, Integer num, ChapterContentInfo contentInfo){
		var pageWordCounts = contentInfo.getPageWordCounts();
		List<PageCodeAndStartPos> resultPair = new ArrayList<>();
		Integer curTotal = 0;

		for (int i = 0; i < pageWordCounts.size(); i++) {
			if (startPos == 0 || (curTotal < startPos && curTotal + pageWordCounts.get(i) >= startPos)) {
				resultPair.add(PageCodeAndStartPos.builder().pageCode(i).startPos(startPos - curTotal)
						.pageWordCount(pageWordCounts.get(i)).build());
				startPos += num;
				for(int j = i; j<pageWordCounts.size(); j++){
					if (curTotal < startPos && curTotal + pageWordCounts.get(j) >= startPos){
						resultPair.add(PageCodeAndStartPos.builder().pageCode(j).startPos(startPos - curTotal)
								.pageWordCount(pageWordCounts.get(i)).build());
						return resultPair;
					}
					curTotal += pageWordCounts.get(i);
				}
				break;
			}
			curTotal += pageWordCounts.get(i);
		}
		throw BizException.badRequest(ExMsg.PARAM_ERROR_FOR_CONTENT);
	}
}
