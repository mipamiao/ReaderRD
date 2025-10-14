package com.mipa.service;

import com.mipa.common.Constant.ExMsg;
import com.mipa.common.Enum.WriterWSOp;
import com.mipa.common.callback.TaskInLock;
import com.mipa.common.configuration.MyConfiguration;
import com.mipa.common.dto.writerwsdto.WriterCommand;
import com.mipa.common.dto.writerwsdto.WriterCommandSet;
import com.mipa.common.exception.BizException;
import com.mipa.mapper.ChapterContentPageMapper;
import com.mipa.mapper.ChapterMapper;
import com.mipa.model.Chapter;
import com.mipa.model.ChapterContentPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContentPageCacheService {


	@Autowired
	private ChapterContentPageMapper pageMapper;

	@Autowired
	private ChapterMapper chapterMapper;

	@Autowired
	private MyConfiguration config;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private RedisLockServer redisLock;



	private static  final String contentPageCachePrefix = "contentPageCachePrefix_" ;
	private static  final String chapterCachePrefix = "chapterCachePrefix_" ;
	private static  final String writerCommandsCachePrefix = "writerCommandsCachePrefix_" ;
	private static  final String AllActiveCacheKey = "AllActiveCacheKeyInContentPageCacheService" ;

	private static final String lockKeyPrefix = "ContentPageCacheServiceLockKey_";

	private ThreadLocal<Map<String, OpRecord>> opRecordTL = new ThreadLocal<>();

	public Chapter getChapter(String chapterId) {
		var object = redisTemplate.opsForValue().get(getChapterKey(chapterId));
		if(object == null){
			var chapterOpt = chapterMapper.selectById(chapterId, true);
			if (chapterOpt.isPresent()) {
				redisTemplate.opsForValue().set(getChapterKey(chapterId), chapterOpt.get());
				return chapterOpt.get();
			}
			throw BizException.badRequest(ExMsg.CHAPTER_NOT_EXIST);
		}
		else return (Chapter)object;
	}

	public void updateChapter(Chapter chapter) {
		try {
			chapterMapper.update(chapter);
			redisTemplate.opsForValue().set(getChapterKey(chapter.getId()), chapter);
			writeMapToDB();
		} catch (DataIntegrityViolationException e) {
			throw BizException.badRequest(ExMsg.DB_CONSTRAIN_FAILED, e);
		}
	}

	public void insertPage(ChapterContentPage page) {
		getOpMap().put(page.getId(), new OpRecord(OpType.Insert, page));
		redisTemplate.opsForValue().set(getKey(page.getId()), page, config.contentPageCacheExpireTime, TimeUnit.SECONDS);
	}

	public void updatePage(ChapterContentPage page) {
		getOpMap().put(page.getId(), new OpRecord(OpType.Update, page));
		redisTemplate.opsForValue().set(getKey(page.getId()), page, config.contentPageCacheExpireTime, TimeUnit.SECONDS);
	}

	public void deletePage(String pageId) {
		getOpMap().put(pageId, new OpRecord(OpType.Delete, null));
		redisTemplate.delete(getKey(pageId));
	}

	public void deletePageBatch(List<String> pageIds) {
		if(pageIds.isEmpty())return;
		for(var id : pageIds)
			getOpMap().put(id, new OpRecord(OpType.Delete, null));
		redisTemplate.delete(pageIds.stream().map(this::getKey).toList());
	}

	public ChapterContentPage getPage(String pageId) {
		var object = redisTemplate.opsForValue().get(getKey(pageId));
		if (object == null) {
			var page = pageMapper.selectById(pageId).get();
			redisTemplate.opsForValue().set(getKey(pageId), page, config.contentPageCacheExpireTime, TimeUnit.SECONDS);
			return page;
		}
		if (object instanceof ChapterContentPage page) {
			return page;
		}
		throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.JSON_PARSE_FAILED_IN_REDIS);
	}

	public boolean cacheOrExecute(WriterCommandSet commandSet, String userId, String chapterId) {
		for(var command : commandSet.getCommands())
			if(command.getType() == WriterWSOp.GetPage||command.getType()==WriterWSOp.GetPageInfo)return false;
		var key = getWriterCommandsKey(userId, chapterId);
		AtomicReference<Boolean> result = new AtomicReference<>(false);
		redisLock.tryLock(getLockKey(key), "TRUE", 5, new TaskInLock(){
			@Override
			public void run() {

				Integer dataCount = (Integer) Optional.ofNullable(redisTemplate.opsForHash()
						.get(key, "dataCount")).orElse(0);

				WriterCommandSet preCommandSet = (WriterCommandSet) redisTemplate.opsForHash()
						.get(key, "preCommandSet");

				if (preCommandSet == null) {
					preCommandSet = WriterCommandSet.builder().timeStamp(commandSet.getTimeStamp()).commands(new ArrayList<WriterCommand>()).build();
				}
				for (var command : commandSet.getCommands()) {
					switch (command.getType()) {
						case UpdatePage_Add -> dataCount += command.getData().length();
						case UpdatePage_Replace -> dataCount += command.getData().length() + command.getNum();
						case UpdatePage_Remove -> dataCount += command.getNum();
					}
				}
				preCommandSet.getCommands().addAll(commandSet.getCommands());

				if (dataCount > config.contentPageCacheDataMaxSize) {
					commandSet.setTimeStamp(preCommandSet.getTimeStamp());
					commandSet.setCommands(preCommandSet.getCommands());
					redisTemplate.delete(key);
					redisTemplate.opsForSet().remove(AllActiveCacheKey, key);
					result.set(false);
				} else {
					commandSet.getCommands().clear();
					redisTemplate.opsForHash().put(key, "dataCount", dataCount);
					redisTemplate.opsForHash().put(key, "preCommandSet", preCommandSet);
					redisTemplate.opsForSet().add(AllActiveCacheKey, key);
					result.set(true);
				}
			}
		}, TimeUnit.SECONDS);

		return result.get();
	}

	public List<String> getAllActiveKeys() {
		Set<Object> members = redisTemplate.opsForSet().members(AllActiveCacheKey);
		if (members == null) {
			return Collections.emptyList();
		}
		return members.stream().map(Object::toString).collect(Collectors.toList());
	}

	public WriterCommandSet getWriterCommandSet(String key) {
		return (WriterCommandSet) redisTemplate.opsForHash().get(key, "preCommandSet");
	}

	private String getKey(String pageId){
		return contentPageCachePrefix + pageId;
	}

	private String getChapterKey(String chapterId){
		return chapterCachePrefix + chapterId;
	}

	private String getWriterCommandsKey(String userId, String chapterId){
		return writerCommandsCachePrefix + userId + "_" + chapterId;
	}

	private String getLockKey(String originKey){
		return lockKeyPrefix + originKey;
	}

	private Map<String, OpRecord> getOpMap() {
		if (opRecordTL.get() == null) {
			opRecordTL.set(new HashMap<String, OpRecord>());
		}
		return opRecordTL.get();
	}

	private void writeMapToDB() {
		var insertList = new ArrayList<ChapterContentPage>();
		var updateList = new ArrayList<ChapterContentPage>();
		var deleteList = new ArrayList<String>();

		var opMap = getOpMap();
		for (var entry : opMap.entrySet()) {
			String key = entry.getKey();
			OpRecord opRecord = entry.getValue();
			switch (opRecord.type) {
				case Insert -> insertList.add(opRecord.page);
				case Update -> updateList.add(opRecord.page);
				case Delete -> deleteList.add(key);
			}
		}
		if (!insertList.isEmpty())
			pageMapper.insertBatch(insertList);
		if (!updateList.isEmpty())
			pageMapper.updateBatch(updateList);
		if (!deleteList.isEmpty())
			pageMapper.deleteBatch(deleteList);
		opMap.clear();
		opRecordTL.remove();
	}

	enum OpType {
		Insert, Update, Delete
	}

	record OpRecord(OpType type, ChapterContentPage page){};

	record WriterCommandAndDataCount(Integer dataCount, WriterCommandSet commandSet){};
}
