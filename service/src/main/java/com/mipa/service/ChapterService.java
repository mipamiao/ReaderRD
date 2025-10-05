package com.mipa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mipa.common.Enum.OrderEnum;
import com.mipa.common.dto.chapterdto.ChapterInfoAndContentDTO;
import com.mipa.common.dto.chapterdto.ChapterInfoDTO;
import com.mipa.common.dto.chapterdto.ChapterRequestDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
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
        var chapterOpt = chapterMapper.selectInfoByBookIdAndOrder(dto.getBookId(), dto.getChapterOrder());
        if (chapterOpt.isPresent()) return null;

        var bookOpt = bookMapper.selectById(dto.getBookId());
        if (bookOpt.isEmpty()) return null;

        bookMapper.updateChapterCount(bookOpt.get().getId(), bookOpt.get().getChapterCount() + 1);
        var chapter = CopyProperties.run(dto, Chapter.class);
        chapter.setId(IdUtil.uuid());
        chapterMapper.insert(chapter);
        var result = chapterMapper.selectInfoById(chapter.getId());
        var chapterInfoDto = CopyProperties.run(result.get(), ChapterInfoDTO.class);
        chapterInfoDto.setChapterId(chapter.getId());
        chapterInfoDto.setAuthorId(dto.getAuthorId());
        return chapterInfoDto;
    }

    @Transactional
    @Override
    public Boolean updateChapter(ChapterRequestDTO dto, String chapterId) {
        var resultData = checkBookIdAndAuthorId(dto.getBookId(), dto.getAuthorId(), chapterId);
        if (resultData.result) {
            var chapter = resultData.chapter;
            chapter.setName(dto.getName());
            chapter.setContent(dto.getContent());
            chapter.setChapterOrder(dto.getChapterOrder());
            chapter.setUpdatedAt(LocalDateTime.now());
            chapterMapper.update(chapter);
            bookMapper.updateUpdatedAtById(resultData.book.getId());
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public Boolean deleteChapter(String authorId, String bookId, String chapterId) {
        var resultData = checkBookIdAndAuthorId(bookId, authorId, chapterId);
        if (resultData.result) {
            var chapter = resultData.chapter;
            if (Objects.equals(chapter.getBookId(), bookId) && resultData.book.getChapterCount() > 0) {
                bookMapper.updateChapterCount(resultData.book.getId(), resultData.book.getChapterCount() - 1);
                chapterMapper.delete(chapterId);
                return true;
            }
        }
        return false;
    }

    @Override
    public ChapterInfoDTO getChapterInfo(String bookId, String chapterId) {
        var chapterOpt = chapterMapper.selectInfoById(chapterId);
        if (chapterOpt.isPresent()) {
            var chapter = chapterOpt.get();
            if (Objects.equals(chapter.getBookId(), bookId)) {
                var chapterInfoDto = CopyProperties.run(chapter, ChapterInfoDTO.class);
                chapterInfoDto.setChapterId(chapter.getId());
                return chapterInfoDto;
            }
        }
        return null;
    }

    @Override
    public PageRecord<ChapterInfoDTO> listChapters(String bookId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        var chapters = chapterMapper.selectInfoAllByBookId(bookId, OrderEnum.CHAPTER_ORDER, OrderEnum.DESC);
        var pageInfo = new PageInfo<>(chapters);
        var infos = pageInfo.getList().stream().map(chapter->{
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
        var infos = pageInfo.getList().stream().map(chapter->{
            var chapterInfoDto = CopyProperties.run(chapter, ChapterInfoDTO.class);
            chapterInfoDto.setChapterId(chapter.getId());
            return chapterInfoDto;
        }).toList();
        return infos;
    }

    //todo 这里content要是很大的话，可能会有性能问题
    @Override
    public ChapterInfoAndContentDTO getChapterInfoAndContent(String bookId, String chapterId) {
        var chapterOpt = chapterMapper.selectById(chapterId);
        if(chapterOpt.isPresent()){
            var chapter = chapterOpt.get();
            if(Objects.equals(chapter.getBookId(), bookId)){
                var chapterInfoAndContentDto = new ChapterInfoAndContentDTO();
                chapterInfoAndContentDto.setChapterInfoDTO(CopyProperties.run(chapter, ChapterInfoDTO.class));
                chapterInfoAndContentDto.getChapterInfoDTO().setChapterId(chapter.getId());
                chapterInfoAndContentDto.setContent(chapter.getContent());
                return chapterInfoAndContentDto;
            }
        }
        return null;
    }

    @Override
    public ChapterInfoAndContentDTO getChapterInfoAndContent(String bookId, Integer order){
        var chapterOpt = chapterMapper.selectByBookIdAndOrder(bookId, order);
        if(chapterOpt.isPresent()){
            var chapter = chapterOpt.get();
            if (Objects.equals(chapter.getBookId(), bookId)) {
                var chapterInfoAndContentDto = new ChapterInfoAndContentDTO();
                chapterInfoAndContentDto.setChapterInfoDTO(CopyProperties.run(chapter, ChapterInfoDTO.class));
                chapterInfoAndContentDto.getChapterInfoDTO().setChapterId(chapter.getId());
                chapterInfoAndContentDto.setContent(chapter.getContent());
                return chapterInfoAndContentDto;
            }
        }
        return null;
    }


    //这个函数用来确保书是作者的，并且章节是书的
    private ResultData checkBookIdAndAuthorId(String bookId, String authorId, String chapterId) {
        var userOpt = userMapper.selectById(authorId);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            var bookOpt = bookMapper.selectById(bookId);
            if (bookOpt.isPresent()) {
                var book = bookOpt.get();
                if (Objects.equals(book.getAuthorId(), user.getId())) {
                    if (chapterId != null) {
                        var chapterOpt = chapterMapper.selectById(chapterId);
                        if (chapterOpt.isPresent()) {
                            var chapter = chapterOpt.get();
                            if (Objects.equals(chapter.getBookId(), bookId)) {
                                return new ResultData(true, user, book, chapter);
                            }
                        }
                    } else {
                        return new ResultData(true, user, book, null);
                    }
                }
            }
        }
        return new ResultData(false, null, null, null);
    }

    record ResultData(Boolean result, User user, Book book, Chapter chapter){}

}
