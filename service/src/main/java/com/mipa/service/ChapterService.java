package com.mipa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mipa.common.Constant.ExMsg;
import com.mipa.common.Enum.OrderEnum;
import com.mipa.common.dto.chapterdto.ChapterInfoDTO;
import com.mipa.common.dto.chapterdto.ChapterRequestDTO;
import com.mipa.common.exception.BizException;
import com.mipa.common.utils.ChapterContentInfo;
import com.mipa.common.utils.CopyProperties;
import com.mipa.common.utils.PageRecord;
import com.mipa.mapper.BookMapper;
import com.mipa.mapper.ChapterContentPageMapper;
import com.mipa.mapper.ChapterMapper;
import com.mipa.mapper.UserMapper;
import com.mipa.model.*;
import com.mipa.service.api.IChapterService;

import java.time.LocalDateTime;
import java.util.List;

import com.mipa.utils.IdUtil;
import com.mipa.validate.VerifyRelationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//todo save到update的转变
@Service
public class ChapterService implements IChapterService {

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private ChapterContentPageMapper pageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    @Transactional
    @Override
    public ChapterInfoDTO addChapter(ChapterRequestDTO dto, String authorId) {
        var vr = VerifyRelationShip.start()
                .verifyAuthorAndBook(authorId, dto.getBookId(), bookMapper);
        if(vr.isSucceed()){
            try {
                //bookMapper.incChapterCount(dto.getBookId());
                var chapter = CopyProperties.run(dto, Chapter.class);
                chapter.setId(IdUtil.uuid());

                var page = ChapterContentPage.builder().chapterId(chapter.getId()).id(IdUtil.uuid()).data("").build();
                var chapterInfo = ChapterContentInfo.builder().pageIds(List.of(page.getId())).build();
                chapter.setContentInfo(chapterInfo);
                chapter.setIsPublish(false);

                chapterMapper.insert(chapter);
                pageMapper.insert(page);

                var result = chapterMapper.selectInfoById(chapter.getId());
                var chapterInfoDto = CopyProperties.run(result.get(), ChapterInfoDTO.class);
                chapterInfoDto.setChapterId(chapter.getId());
                //chapterInfoDto.setAuthorId(dto.getAuthorId());
                return chapterInfoDto;
            } catch (DataIntegrityViolationException e) {
                throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_HAVEN_EXIST));
            }
        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }

    }

    @Transactional
    @Override
    public void updateChapter(ChapterRequestDTO dto, String authorId, String chapterId) {

        var vr = VerifyRelationShip.start()
                .verifyAuthorAndBook(authorId, dto.getBookId(), bookMapper)
                .verifyBookAndChapter(dto.getBookId(), chapterId, chapterMapper)
                .verifyChapterIdAndOrder(dto.getChapterOrder(), chapterId, chapterMapper);

        if(vr.isSucceed()){
            var chapter = vr.get(Chapter.class);
            var book = vr.get(Book.class);
            chapter.setName(dto.getName());
            chapter.setChapterOrder(dto.getChapterOrder());
            chapter.setUpdatedAt(LocalDateTime.now());
            try{
                chapterMapper.update(chapter);
                bookMapper.updateUpdatedAtById(book.getId());
            }catch (DataIntegrityViolationException e){
                throw BizException.badRequest(ExMsg.DB_CONSTRAIN_FAILED);
            }
        }else{
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }
    }

    @Transactional
    public void updatePublishState(String userId, String chapterId, Boolean publishState) {
        var vr = VerifyRelationShip.start().verifyAuthorAndChapter(userId, chapterId, chapterMapper, bookMapper);
        if (vr.isSucceed()) {
            try {
                int n = chapterMapper.updatePublishState(chapterId, publishState);
                if (n > 0) {
                    var bookId = vr.get(Book.class).getId();
                    if (publishState == true) bookMapper.incChapterCount(bookId);
                    else bookMapper.decChapterCount(bookId);
                }
            } catch (DataIntegrityViolationException e) {
                throw BizException.badRequest(ExMsg.DB_CONSTRAIN_FAILED);
            }
        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }
    }

    @Override
    @Transactional
    public ChapterInfoDTO copyChapter(String userId, String chapterId) {
        var vr = VerifyRelationShip.start().verifyAuthorAndChapter(userId, chapterId, chapterMapper, bookMapper);
        if (vr.isSucceed()) {
            var oldChapter = vr.get(Chapter.class);
            var chapter = CopyProperties.run(oldChapter, Chapter.class);
            chapter.setIsPublish(false);
            chapter.setId(IdUtil.uuid());
            var pages = chapter.getContentInfo().getPageIds().stream().map(pageId -> {
                var oldPage = pageMapper.selectById(pageId);
                var newPage = CopyProperties.run(oldPage, ChapterContentPage.class);
                newPage.setId(IdUtil.uuid());
                return newPage;
            }).toList();
            chapter.getContentInfo().setPageIds(pages.stream().map(ChapterContentPage::getId).toList());
            pageMapper.insertBatch(pages);
            var chapterInfoDto = CopyProperties.run(chapter, ChapterInfoDTO.class);
            chapterInfoDto.setChapterId(chapter.getId());
            //chapterInfoDto.setAuthorId(dto.getAuthorId());
            return chapterInfoDto;
        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }
    }

    @Transactional
    @Override
    public void deleteChapter(String authorId,  String chapterId) {
        var vr = VerifyRelationShip.start()
                .verifyAuthorAndChapter(authorId, chapterId, chapterMapper, bookMapper);
        if(vr.isSucceed()){
            try{
                bookMapper.decChapterCount(vr.get(Book.class).getId());
                chapterMapper.delete(chapterId);
                pageMapper.deleteBatch(vr.get(Chapter.class).getContentInfo().getPageIds());
            }catch (DataIntegrityViolationException e){
                throw BizException.badRequest(ExMsg.DB_CONSTRAIN_FAILED);
            }
        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }
    }

    @Override
    public ChapterInfoDTO getPublishedChapterInfo(String chapterId) {
        return getChapterInfo(chapterId, false);
    }

    @Override
    public ChapterInfoDTO getWholeChapterInfo(String authorId, String chapterId) {
        var vr = VerifyRelationShip.start()
                .verifyAuthorAndChapter(authorId, chapterId, chapterMapper, bookMapper);
        if (vr.isSucceed()) {
            return getChapterInfo(chapterId, true);
        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.CHAPTER_BOOK_MISMATCH));
        }
    }

    @Override
    public ChapterInfoDTO getPublishedChapterInfo(String bookId, Integer chapterOrder) {
        return getChapterInfo(bookId, chapterOrder, false);
    }

    @Override
    public ChapterInfoDTO getWholeChapterInfo(String authorId, String bookId, Integer chapterOrder) {
        var vr = VerifyRelationShip.start()
                .verifyAuthorAndBook(authorId, bookId, bookMapper);
        if (vr.isSucceed()) {
            return getChapterInfo(bookId, chapterOrder, true);
        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.CHAPTER_BOOK_MISMATCH));
        }
    }

    @Override
    public PageRecord<ChapterInfoDTO> listWholeChapters(String authorId, String bookId, Integer pageNum, Integer pageSize){
        var vr  = VerifyRelationShip.start()
                .verifyAuthorAndBook(authorId, bookId, bookMapper);
        if(vr.isSucceed()){
            return listChapters(bookId, pageNum, pageSize, true);
        }else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }
    }

    @Override
    public PageRecord<ChapterInfoDTO> listPublishedChapters(String bookId, Integer pageNum, Integer pageSize) {
        return listChapters(bookId, pageNum, pageSize, false);
    }


    @Override
    public List<ChapterInfoDTO> listAllPublishedChapters(String bookId) {
        return listAllChapters(bookId, false);
    }

    @Override
    public List<ChapterInfoDTO> listAllWholeChapters(String authorId, String bookId) {
        var vr = VerifyRelationShip.start()
                .verifyAuthorAndBook(authorId, bookId, bookMapper);
        if (vr.isSucceed()) {
            return listAllChapters(bookId, true);
        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }
    }

    private ChapterInfoDTO getChapterInfo(String bookId, Integer chapterOrder, Boolean includeUnpublished) {
        var chapterInfoOpt = chapterMapper.selectByBookIdAndOrder(bookId, chapterOrder, includeUnpublished);
        if(chapterInfoOpt.isPresent()){
            var chapterInfoDto = CopyProperties.run(chapterInfoOpt.get(), ChapterInfoDTO.class);
            chapterInfoDto.setChapterId(chapterInfoOpt.get().getId());
            return chapterInfoDto;
        }else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.CHAPTER_BOOK_MISMATCH));
        }
    }

    private ChapterInfoDTO getChapterInfo(String chapterId, Boolean includeUnpublished) {
        var chapterInfoOpt = chapterMapper.selectById(chapterId, includeUnpublished);
        if(chapterInfoOpt.isPresent()){
            var chapterInfoDto = CopyProperties.run(chapterInfoOpt.get(), ChapterInfoDTO.class);
            chapterInfoDto.setChapterId(chapterInfoOpt.get().getId());
            return chapterInfoDto;
        }else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.CHAPTER_BOOK_MISMATCH));
        }
    }

    private PageRecord<ChapterInfoDTO> listChapters(String bookId, Integer pageNum, Integer pageSize, Boolean includeUnpublished) {
        PageHelper.startPage(pageNum, pageSize);
        var chapters = chapterMapper.selectInfoAllByBookId(bookId, includeUnpublished, OrderEnum.CHAPTER_ORDER, OrderEnum.DESC);
        var pageInfo = new PageInfo<>(chapters);
        var infos = pageInfo.getList().stream().map(chapter -> {
            var chapterInfoDto = CopyProperties.run(chapter, ChapterInfoDTO.class);
            chapterInfoDto.setChapterId(chapter.getId());
            return chapterInfoDto;
        }).toList();
        return PageRecord.of(infos, pageInfo);
    }


    private List<ChapterInfoDTO> listAllChapters(String bookId, Boolean includeUnpublished) {
        var chapters = chapterMapper.selectInfoAllByBookId(bookId, includeUnpublished, OrderEnum.CHAPTER_ORDER, OrderEnum.DESC);
        var pageInfo = new PageInfo<>(chapters);
        var infos = pageInfo.getList().stream().map(chapter -> {
            var chapterInfoDto = CopyProperties.run(chapter, ChapterInfoDTO.class);
            chapterInfoDto.setChapterId(chapter.getId());
            return chapterInfoDto;
        }).toList();
        return infos;
    }



}
