package com.mipa.service;

import com.mipa.common.configuration.MyConfiguration;
import com.mipa.common.dto.writerwsdto.*;
import com.mipa.mapper.ChapterContentPageMapper;
import com.mipa.mapper.ChapterMapper;
import com.mipa.model.Chapter;
import com.mipa.model.ChapterContentPage;
import com.mipa.service.api.IContentPageService;
import com.mipa.utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ContentPageService implements IContentPageService {

	@Autowired
	ChapterContentPageMapper pageMapper;

	@Autowired
	ChapterMapper chapterMapper;

	@Autowired
	MyConfiguration config;

	@Transactional
	public ServerCommandSet scheduleOp(WriterCommandSet commands, String userId, String chapterId) {
		var serverCommands = ServerCommandSet.builder().commands(new ArrayList<ServerCommand>()).build();
		var chapter = chapterMapper.selectById(chapterId, true).get();
		for (var command : commands.getCommands()) {
			switch (command.getType()) {
				case GetPageInfo -> {
					getInfoOp(userId, chapter, command, serverCommands);
				}
				case GetPage -> {
					getOp(userId, chapter, command, serverCommands);
				}
				case InsertPage -> {
					insertOp(userId, chapter, command, serverCommands);
				}
				case DeletePage -> {
					deleteOp(userId, chapter, command, serverCommands);
				}
				case MergePage -> {
					mergeOp(userId, chapter, command, serverCommands);
				}
				case UpdatePage_Add -> {
					updateAddOp(userId, chapter, command, serverCommands);
				}
				case UpdatePage_Remove -> {
					updateRemoveOp(userId, chapter, command, serverCommands);
				}
				case UpdatePage_Replace -> {
					updateReplaceOp(userId, chapter, command, serverCommands);
				}
				default -> {
					break;
				}
			}
		}
		return serverCommands;
	}

	public void getOp(String userId, Chapter chapter , WriterCommand command, ServerCommandSet serverCommands){
		var page = getPage(chapter.getContentInfo().getPageIds().get(command.getStartPos()));
		serverCommands.getCommands().add(new ServerCommand(command.getStartPos(), page.getId(), page.getData()));
	}

	public void getInfoOp(String userId, Chapter chapter , WriterCommand command, ServerCommandSet serverCommands){
		serverCommands.getCommands().add(ServerCommand.builder().contentInfo(chapter.getContentInfo()).build());
	}


	private void insertOp(String userId, Chapter chapter , WriterCommand command, ServerCommandSet serverCommands){
		var page = new ChapterContentPage();
		page.setData(command.getData());
		page.setId(IdUtil.uuid());
		page.setChapterId(chapter.getId());
		serverCommands.getCommands().add(new ServerCommand(command.getStartPos(), page.getId()));
		insertPage(page);
		chapter.getContentInfo().getPageIds().add(command.getStartPos(), page.getId());
	}

	private void deleteOp(String userId, Chapter chapter , WriterCommand command, ServerCommandSet serverCommands){
		var subList = chapter.getContentInfo().getPageIds().subList(command.getStartPos(), command.getStartPos() + command.getNum());
		deletePageBatch(subList);
		subList.clear();
	}

	private void mergeOp(String userId, Chapter chapter , WriterCommand command, ServerCommandSet serverCommands){
		String id2 = chapter.getContentInfo().getPageIds().get(command.getOtherPos());
		String id1 = chapter.getContentInfo().getPageIds().get(command.getStartPos());
		var page2 = getPage(id2);
		var page1 = getPage(id1);
		var newData = page1.getData() + page2.getData();
		page1.setData(newData);
		deletePage(page2.getId());
		updatePage(page1);
	}

	private void updateAddOp(String userId, Chapter chapter , WriterCommand command, ServerCommandSet serverCommands){
		var page = getPage(chapter.getContentInfo().getPageIds().get(command.getStartPos()));
		StringBuilder builder = new StringBuilder(page.getData());
		builder.insert(command.getOtherPos(), command.getData());
		page.setData(builder.toString());
		updatePage(page);
	}

	private void updateRemoveOp(String userId, Chapter chapter , WriterCommand command, ServerCommandSet serverCommands){
		var page = getPage(chapter.getContentInfo().getPageIds().get(command.getStartPos()));
		StringBuilder builder = new StringBuilder(page.getData());
		builder.delete(command.getOtherPos(), command.getOtherPos() + command.getNum());
		page.setData(builder.toString());
		updatePage(page);
	}

	private void updateReplaceOp(String userId, Chapter chapter , WriterCommand command, ServerCommandSet serverCommands){
		var page = getPage(chapter.getContentInfo().getPageIds().get(command.getStartPos()));
		StringBuilder builder = new StringBuilder(page.getData());
		builder.replace(command.getOtherPos(), command.getOtherPos() + command.getNum(), command.getData());
		page.setData(builder.toString());
		updatePage(page);
	}


	private void insertPage(ChapterContentPage page){
		pageMapper.insert(page);
	}

	private void updatePage(ChapterContentPage page){
		pageMapper.update(page);
	}

	private void deletePage(String pageId){
		pageMapper.delete(pageId);
	}

	private void deletePageBatch(List<String> pageIds){
		for(var id : pageIds){
			pageMapper.delete(id);
		}
	}

	private ChapterContentPage getPage(String pageId){
		return pageMapper.selectById(pageId).get();
	}

}
