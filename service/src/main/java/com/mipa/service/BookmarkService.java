package com.mipa.service;

import com.mipa.common.bookmarkdto.BookmarkInfoDTO;
import com.mipa.common.bookmarkdto.BookmarkInfoListDTO;
import com.mipa.common.bookmarkdto.BookmarkRequestDTO;
import com.mipa.convert.BookEntityConvert;
import com.mipa.convert.BookmarkEntityConvert;
import com.mipa.model.BookEntity;
import com.mipa.model.ChapterEntity;
import com.mipa.model.UserEntity;
import com.mipa.repository.BookRepository;
import com.mipa.repository.BookmarkRepository;
import com.mipa.repository.ChapterRepository;
import com.mipa.repository.UserRepository;
import com.mipa.service.api.IBookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BookmarkService implements IBookmarkService {

    @Autowired
    private ChapterRepository chapterRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private BookmarkRepository bookmarkRepo;

    @Override
    public BookmarkInfoDTO addBookmark(BookmarkRequestDTO dto, String userId) {
        var res = checkParam(dto.getBookId(), dto.getChapterId());
        if(res.result){
            var bookmarkOpt = bookmarkRepo.findByUserIdAndBookIdAndChapterId(userId, dto.getBookId(), dto.getChapterId());
            if(bookmarkOpt.isEmpty()){
                var entity = BookmarkEntityConvert.fromBookmarkRequestDTO(dto);
                entity.setCreateTime(LocalDateTime.now());
                entity.setUserId(userId);
                entity.setUpdateTime(LocalDateTime.now());
                entity = bookmarkRepo.save(entity);
                return BookmarkEntityConvert.toBookmarkInfoDTO(entity);
            }
        }
        return null;
    }

    @Override
    public Boolean updateBookmark(BookmarkRequestDTO dto, String bookmarkId, String userId) {
        var res = checkParam(dto.getBookId(), dto.getChapterId());
        if(res.result){
            var  bookmarkOpt = bookmarkRepo.findById(bookmarkId);
            if(bookmarkOpt.isPresent()&& Objects.equals(bookmarkOpt.get().getUserId(), userId)){
                var bookmark = bookmarkOpt.get();
                bookmark.setNote(dto.getNote());
                bookmark.setUpdateTime(LocalDateTime.now());
                bookmarkRepo.save(bookmark);
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean delBookmark(String bookmarkId, String userId) {
        var  bookmarkOpt = bookmarkRepo.findById(bookmarkId);
        if(bookmarkOpt.isPresent()&& Objects.equals(bookmarkOpt.get().getUserId(), userId)){
            var bookmark = bookmarkOpt.get();
            bookmarkRepo.delete(bookmark);
            return true;
        }
        return false;
    }

    @Override
    public List<BookmarkInfoDTO> listAllBookmark(String userId, String bookId) {
        return bookmarkRepo.findByUserIdAndBookId(userId, bookId).stream().map(BookmarkEntityConvert::toBookmarkInfoDTO).toList();
    }

    @Override
    public Page<BookmarkInfoDTO> listBookmark(String userId, String bookId, Integer pageNum, Integer pageSize) {
        return null;
    }

    @Override
    public BookmarkInfoDTO getBookmark(String userId, String bookId, String chapterId){
        var bookmarkOpt = bookmarkRepo.findByUserIdAndBookIdAndChapterId(userId, bookId, chapterId);
        return bookmarkOpt.map(BookmarkEntityConvert::toBookmarkInfoDTO).orElse(null);
    }

    //用来确保
    private ResultData checkParam(String bookId, String chapterId){
        var bookOpt = bookRepo.findById(bookId);
        if (bookOpt.isPresent()) {
            var book = bookOpt.get();
            var chapterOpt = chapterRepo.findById(chapterId);
            if (chapterOpt.isPresent()) {
                var chapter = chapterOpt.get();
                if (Objects.equals(chapter.getBook().getBookId(), bookId)) {
                    return new  ResultData(true, book, chapter);
                }
            }
        }
        return new ResultData(false, null, null);
    }

    record ResultData(Boolean result, BookEntity book, ChapterEntity chapter){};
}
