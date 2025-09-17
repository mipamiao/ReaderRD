package com.mipa.service;

import com.mipa.common.chapterdto.ChapterInfoAndContentDTO;
import com.mipa.common.chapterdto.ChapterInfoDTO;
import com.mipa.common.chapterdto.ChapterRequestDTO;
import com.mipa.convert.BookEntityConvert;
import com.mipa.convert.ChapterEntityConvert;
import com.mipa.model.BookEntity;
import com.mipa.model.ChapterEntity;
import com.mipa.model.UserEntity;
import com.mipa.repository.BookRepository;
import com.mipa.repository.ChapterRepository;
import com.mipa.repository.UserRepository;
import com.mipa.service.api.IChapterService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//todo save到update的转变
@Service
public class ChapterService implements IChapterService {

    @Autowired
    private ChapterRepository chapterRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BookRepository bookRepo;

    @Transactional
    public ChapterInfoDTO addChapter(ChapterRequestDTO dto) {
        var resultData = checkBookIdAndAuthorId(dto.getBookId(), dto.getAuthorId(), null);
        if (resultData.result) {
            var chapterEntity = ChapterEntityConvert.fromChapterRequestDTO(dto);
            chapterEntity.setCreatedAt(LocalDateTime.now());
            chapterEntity.setUpdatedAt(LocalDateTime.now());
            chapterEntity.setBook(resultData.book);
            bookRepo.updateChapterCount(resultData.book.getBookId(), resultData.book.getChaptersCount() + 1, LocalDateTime.now());
            var result = chapterRepo.save(chapterEntity);
            return ChapterEntityConvert.toChapterInfoDTO(result);
        }
        return null;
    }

    public Boolean updateChapter(ChapterRequestDTO dto, String chapterId) {
        var resultData = checkBookIdAndAuthorId(dto.getBookId(), dto.getAuthorId(), chapterId);
        if (resultData.result) {
            var chapter = resultData.chapter;
            chapter.setTitle(dto.getTitle());
            chapter.setContent(dto.getContent());
            chapter.setOrder(dto.getOrder());
            chapter.setUpdatedAt(LocalDateTime.now());
            chapterRepo.save(chapter);
            bookRepo.updatedAt(resultData.book.getBookId(), LocalDateTime.now());
            return true;
        }
        return false;
    }

    @Transactional
    public Boolean deleteChapter(String authorId, String bookId, String chapterId) {
        var resultData = checkBookIdAndAuthorId(bookId, authorId, chapterId);
        if (resultData.result) {
            var chapter = resultData.chapter;
            if (Objects.equals(chapter.getBook().getBookId(), bookId) && resultData.book.getChaptersCount() > 0) {
                bookRepo.updateChapterCount(resultData.book.getBookId(), resultData.book.getChaptersCount() - 1, LocalDateTime.now());
                chapterRepo.delete(chapter);
                return true;
            }
        }
        return false;
    }

    public ChapterInfoDTO getChapterInfo(String bookId, String chapterId) {
        var chapterOpt = chapterRepo.findById(chapterId);
        if (chapterOpt.isPresent()) {
            var chapter = chapterOpt.get();
            if (Objects.equals(chapter.getBook().getBookId(), bookId)) {
                return ChapterEntityConvert.toChapterInfoDTO(chapter);
            }
        }
        return null;
    }

    //todo 以后改为分页查询
    public List<ChapterInfoDTO> listChapters(String bookId) {
        var chapters = chapterRepo.findByBookIdOrderByOrderAsc(BookEntityConvert.specifyBookId(bookId));
        return chapters.stream().map(ChapterEntityConvert::toChapterInfoDTO).toList();
    }

    //todo 这里content要是很大的话，可能会有性能问题
    public ChapterInfoAndContentDTO getChapterInfoAndContent(String bookId, String chapterId) {
        var chapterOpt = chapterRepo.findById(chapterId);
        if (chapterOpt.isPresent()) {
            var chapter = chapterOpt.get();
            if (Objects.equals(chapter.getBook().getBookId(), bookId)) {
                return ChapterEntityConvert.toChapterInfoAndContentDTO(chapter);
            }
        }
        return null;
    }

    //todo 这里因为jpa是懒加载，所以会有n+1问题，后续可以优化
    //这个函数用来确保书是作者的，并且章节是书的
    private ResultData checkBookIdAndAuthorId(String bookId, String authorId, String chapterId) {
        var userOpt = userRepo.findById(authorId);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            var bookOpt = bookRepo.findById(bookId);
            if (bookOpt.isPresent()) {
                var book = bookOpt.get();
                if (Objects.equals(book.getAuthor().getUserId(), user.getUserId())) {
                    if (chapterId != null) {
                        var chapterOpt = chapterRepo.findById(chapterId);
                        if (chapterOpt.isPresent()) {
                            var chapter = chapterOpt.get();
                            if (Objects.equals(chapter.getBook().getBookId(), bookId)) {
                                return new ResultData(true, user, book, chapter);
                            }
                        }
                    }
                    return new ResultData(true, user, book, null);
                }
            }
        }
        return new ResultData(false, null, null, null);
    }

    record ResultData(Boolean result, UserEntity user, BookEntity book, ChapterEntity chapter){}

}
