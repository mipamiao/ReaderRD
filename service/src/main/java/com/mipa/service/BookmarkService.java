package com.mipa.service;

import com.mipa.common.bookmarkdto.BookmarkInfoDTO;
import com.mipa.common.bookmarkdto.BookmarkInfoListDTO;
import com.mipa.common.bookmarkdto.BookmarkRequestDTO;
import com.mipa.common.utils.CopyProperties;
import com.mipa.mapper.BookMapper;
import com.mipa.mapper.ChapterMapper;
import com.mipa.mapper.ReaderBookmarkMapper;
import com.mipa.mapper.UserMapper;
import com.mipa.model.*;
import com.mipa.service.api.IBookmarkService;
import com.mipa.utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BookmarkService implements IBookmarkService {

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private ReaderBookmarkMapper readerBookmarkMapper;

    @Override
    public BookmarkInfoDTO addBookmark(BookmarkRequestDTO dto, String userId) {
        var res = checkParam(dto.getBookId(), dto.getChapterId());
        if(res.result){
            var bookmarkOpt = readerBookmarkMapper.selectByUserIdAndBookIdAndChapterId(userId, dto.getBookId(), dto.getChapterId());
            if(bookmarkOpt.isEmpty()){

                var bookmark = CopyProperties.run(dto, ReaderBookmark.class);
                bookmark.setId(IdUtil.uuid());
                bookmark.setUserId(userId);
                readerBookmarkMapper.insert(bookmark);
                bookmarkOpt = readerBookmarkMapper.selectById(bookmark.getId());
                if(bookmarkOpt.isPresent()){
                    return CopyProperties.run(bookmarkOpt.get(), BookmarkInfoDTO.class);
                }
            }
        }
        return null;
    }

    @Override
    public Boolean updateBookmark(BookmarkRequestDTO dto, String bookmarkId, String userId) {
        var res = checkParam(dto.getBookId(), dto.getChapterId());
        if(res.result){
            var  bookmarkOpt = readerBookmarkMapper.selectById(bookmarkId);
            if(bookmarkOpt.isPresent()&& Objects.equals(bookmarkOpt.get().getUserId(), userId)){
                var bookmark = bookmarkOpt.get();
                bookmark.setNote(dto.getNote());
                readerBookmarkMapper.update(bookmark);
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean delBookmark(String bookmarkId, String userId) {
        var  bookmarkOpt = readerBookmarkMapper.selectById(bookmarkId);
        if(bookmarkOpt.isPresent()&& Objects.equals(bookmarkOpt.get().getUserId(), userId)){
            var bookmark = bookmarkOpt.get();
            readerBookmarkMapper.delete(bookmarkId);
            return true;
        }
        return false;
    }

    @Override
    public List<BookmarkInfoDTO> listAllBookmark(String userId, String bookId) {
        var bookmarks = readerBookmarkMapper.selectByUserIdAndBookId(userId, bookId);
        return bookmarks.stream().map(item -> {
            return CopyProperties.run(item, BookmarkInfoDTO.class);
        }).toList();

    }

//    @Override
//    public Page<BookmarkInfoDTO> listBookmark(String userId, String bookId, Integer pageNum, Integer pageSize) {
//        return null;
//    }

    @Override
    public BookmarkInfoDTO getBookmark(String userId, String bookId, String chapterId) {
        var bookmarkOpt = readerBookmarkMapper.selectByUserIdAndBookIdAndChapterId(userId, bookId, chapterId);
        return bookmarkOpt.map(bookmark -> CopyProperties.run(bookmark, BookmarkInfoDTO.class)).orElse(null);
    }

    //用来确保
    private ResultData checkParam(String bookId, String chapterId) {
        var bookOpt = bookMapper.selectById(bookId);
        if (bookOpt.isPresent()) {
            var book = bookOpt.get();
            var chapterOpt = chapterMapper.selectById(chapterId);
            if (chapterOpt.isPresent()) {
                var chapter = chapterOpt.get();
                if (Objects.equals(chapter.getBookId(), bookId)) {
                    return new ResultData(true, book, chapter);
                }
            }
        }
        return new ResultData(false, null, null);
    }

    record ResultData(Boolean result, Book book, Chapter chapter){};
}
