package com.mipa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mipa.common.Constant.ExMsg;
import com.mipa.common.Enum.OrderEnum;
import com.mipa.common.dto.chapterdto.ChapterInfoAndContentDTO;
import com.mipa.common.dto.chapterdto.ChapterInfoDTO;
import com.mipa.common.dto.chapterdto.ChapterRequestDTO;
import com.mipa.common.exception.BizException;
import com.mipa.common.utils.CopyProperties;
import com.mipa.common.utils.PageRecord;
import com.mipa.mapper.BookMapper;
import com.mipa.mapper.ChapterMapper;
import com.mipa.mapper.UserMapper;
import com.mipa.model.*;
import com.mipa.service.api.IChapterService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.mipa.utils.IdUtil;
import com.mipa.validate.VerifyRelationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//todo save到update的转变
@Service
public class ChapterService implements IChapterService {

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    @Transactional
    @Override
    public ChapterInfoDTO addChapter(ChapterRequestDTO dto) {
        try {
            bookMapper.incChapterCount(dto.getBookId());
            var chapter = CopyProperties.run(dto, Chapter.class);
            chapter.setId(IdUtil.uuid());
            chapterMapper.insert(chapter);
            var result = chapterMapper.selectInfoById(chapter.getId());
            var chapterInfoDto = CopyProperties.run(result.get(), ChapterInfoDTO.class);
            chapterInfoDto.setChapterId(chapter.getId());
            //chapterInfoDto.setAuthorId(dto.getAuthorId());
            return chapterInfoDto;
        } catch (DataIntegrityViolationException e) {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_HAVEN_EXIST));
        }

    }

    @Transactional
    @Override
    public void updateChapter(ChapterRequestDTO dto, String chapterId) {

        var vr = VerifyRelationShip.start()
                .verifyAuthorAndBook(dto.getAuthorId(), dto.getBookId(), bookMapper)
                .verifyBookAndChapter(dto.getBookId(), chapterId, chapterMapper)
                .verifyChapterIdAndOrder(dto.getChapterOrder(), chapterId, chapterMapper);

        if(vr.isSucceed()){
            var chapter = vr.get(Chapter.class);
            var book = vr.get(Book.class);
            chapter.setName(dto.getName());
            chapter.setContent(dto.getContent());
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
    @Override
    public void deleteChapter(String authorId, String bookId, String chapterId) {
        var vr = VerifyRelationShip.start()
                .verifyAuthorAndBook(authorId, bookId, bookMapper)
                .verifyBookAndChapter(bookId, chapterId, chapterMapper);
        if(vr.isSucceed()){
            try{
                bookMapper.decChapterCount(bookId);
                chapterMapper.delete(chapterId);
            }catch (DataIntegrityViolationException e){
                throw BizException.badRequest(ExMsg.DB_CONSTRAIN_FAILED);
            }
        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.NOT_AUTHOR));
        }
    }

    @Override
    public ChapterInfoDTO getChapterInfo(String bookId, String chapterId) {
        var vr = VerifyRelationShip.start()
                .verifyBookAndChapter(bookId, chapterId, chapterMapper);
        if(vr.isSucceed()){
            var chapter = vr.get(Chapter.class);
            var chapterInfoDto = CopyProperties.run(chapter, ChapterInfoDTO.class);
            chapterInfoDto.setChapterId(chapter.getId());
            return chapterInfoDto;
        }else{
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.CHAPTER_BOOK_MISMATCH));
        }
    }

    @Override
    public PageRecord<ChapterInfoDTO> listChapters(String bookId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        var chapters = chapterMapper.selectInfoAllByBookId(bookId, OrderEnum.CHAPTER_ORDER, OrderEnum.DESC);
        var pageInfo = new PageInfo<>(chapters);
        var infos = pageInfo.getList().stream().map(chapter -> {
            var chapterInfoDto = CopyProperties.run(chapter, ChapterInfoDTO.class);
            chapterInfoDto.setChapterId(chapter.getId());
            return chapterInfoDto;
        }).toList();
        return PageRecord.of(infos, pageInfo);
    }

    @Override
    public List<ChapterInfoDTO> listAllChapters(String bookId) {
        var chapters = chapterMapper.selectInfoAllByBookId(bookId, OrderEnum.CHAPTER_ORDER, OrderEnum.DESC);
        var pageInfo = new PageInfo<>(chapters);
        var infos = pageInfo.getList().stream().map(chapter -> {
            var chapterInfoDto = CopyProperties.run(chapter, ChapterInfoDTO.class);
            chapterInfoDto.setChapterId(chapter.getId());
            return chapterInfoDto;
        }).toList();
        return infos;
    }

    //todo 这里content要是很大的话，可能会有性能问题
    @Override
    public ChapterInfoAndContentDTO getChapterInfoAndContent(String bookId, String chapterId) {
        var vr = VerifyRelationShip.start()
                .verifyBookAndChapter(bookId, chapterId, chapterMapper);
        if (vr.isSucceed()) {
            var chapter = vr.get(Chapter.class);
            var chapterInfoAndContentDto = new ChapterInfoAndContentDTO();
            chapterInfoAndContentDto.setChapterInfoDTO(CopyProperties.run(chapter, ChapterInfoDTO.class));
            chapterInfoAndContentDto.getChapterInfoDTO().setChapterId(chapter.getId());
            chapterInfoAndContentDto.setContent(chapter.getContent());
            return chapterInfoAndContentDto;
        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.CHAPTER_BOOK_MISMATCH));
        }
    }

    @Override
    public ChapterInfoAndContentDTO getChapterInfoAndContent(String bookId, Integer order) {
        var chapterOpt = chapterMapper.selectByBookIdAndOrder(bookId, order);
        if (chapterOpt.isPresent()) {
            var chapter = chapterOpt.get();
            var chapterInfoAndContentDto = new ChapterInfoAndContentDTO();
            chapterInfoAndContentDto.setChapterInfoDTO(CopyProperties.run(chapter, ChapterInfoDTO.class));
            chapterInfoAndContentDto.getChapterInfoDTO().setChapterId(chapter.getId());
            chapterInfoAndContentDto.setContent(chapter.getContent());
            return chapterInfoAndContentDto;
        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOK_NOT_EXIST, ExMsg.CHAPTER_NOT_EXIST, ExMsg.CHAPTER_BOOK_MISMATCH));
        }
    }


}
