package com.mipa.service;

import com.mipa.common.Constant.ExMsg;
import com.mipa.common.vo.BookmarkInfoVO;
import com.mipa.common.dto.bookmarkdto.BookmarkRequestDTO;
import com.mipa.common.exception.BizException;
import com.mipa.common.utils.CopyProperties;
import com.mipa.mapper.BookMapper;
import com.mipa.mapper.ChapterMapper;
import com.mipa.mapper.ReaderBookmarkMapper;
import com.mipa.mapper.UserMapper;
import com.mipa.model.*;
import com.mipa.service.api.IBookmarkService;
import com.mipa.utils.IdUtil;
import com.mipa.validate.VerifyRelationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public BookmarkInfoVO addBookmark(BookmarkRequestDTO dto, String userId) {

        var vr = VerifyRelationShip.start()
                .verifyBookAndChapter(dto.getBookId(), dto.getChapterId(), chapterMapper)
                .verifyChapterIdAndOrder(dto.getChapterOrder(), dto.getChapterId(), chapterMapper);
        if (vr.isSucceed()) {
            try {
                var bookmarkOpt = readerBookmarkMapper.selectByUserIdAndBookIdAndChapterId(userId, dto.getBookId(), dto.getChapterId());
                if (bookmarkOpt.isEmpty()) {
                    var chapter = vr.get(Chapter.class);
                    var bookmark = CopyProperties.run(dto, ReaderBookmark.class);
                    bookmark.setId(IdUtil.uuid());
                    bookmark.setUserId(userId);
                    readerBookmarkMapper.insert(bookmark);
                    var bookmarkInfoOpt = readerBookmarkMapper.selectInfoById(bookmark.getId());
                    if (bookmarkInfoOpt.isPresent()) {
                        return CopyProperties.run(bookmarkInfoOpt.get(), BookmarkInfoVO.class);
                    }
                }
                throw BizException.badRequest(ExMsg.BOOKMARK_HAD_EXIST);
            } catch (DataIntegrityViolationException e) {
                throw BizException.badRequest(ExMsg.DB_CONSTRAIN_FAILED);
            }

        } else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.CHAPTER_BOOK_MISMATCH, ExMsg.CHAPTER_ID_ORDER_MISMATCH));
        }

    }

    @Transactional
    @Override
    public void updateBookmark(BookmarkRequestDTO dto, String bookmarkId, String userId) {
        var vr = VerifyRelationShip.start()
                .verifyBookAndChapter(dto.getBookId(), dto.getChapterId(), chapterMapper)
                .verifyChapterIdAndOrder(dto.getChapterOrder(), dto.getChapterId(), chapterMapper);
        if(vr.isSucceed()){
            try{
                var  bookmarkOpt = readerBookmarkMapper.selectById(bookmarkId);
                if(bookmarkOpt.isPresent()&& Objects.equals(bookmarkOpt.get().getUserId(), userId)){
                    var bookmark = bookmarkOpt.get();
                    bookmark.setNote(dto.getNote());
                    readerBookmarkMapper.update(bookmark);
                }
            }catch (DataIntegrityViolationException e){
                throw BizException.badRequest(ExMsg.DB_CONSTRAIN_FAILED);
            }
        }else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.CHAPTER_BOOK_MISMATCH, ExMsg.CHAPTER_ID_ORDER_MISMATCH));
        }
    }

    @Transactional
    @Override
    public void delBookmark(String bookmarkId, String userId) {
        var vr = VerifyRelationShip.start()
                .verifyBookmarkAndUser(userId, bookmarkId, readerBookmarkMapper);

        if(vr.isSucceed()){
            try{
                readerBookmarkMapper.delete(bookmarkId);
            }catch (DataIntegrityViolationException e){
                throw BizException.badRequest(ExMsg.DB_CONSTRAIN_FAILED);
            }
        }else {
            throw BizException.badRequest(ExMsg.Or(ExMsg.BOOKMARK_USER_MISMATCH));
        }
    }

    @Override
    public List<BookmarkInfoVO> listAllBookmark(String userId, String bookId) {
        return readerBookmarkMapper.selectInfoByUserIdAndBookId(userId, bookId);
    }

//    @Override
//    public Page<BookmarkInfoDTO> listBookmark(String userId, String bookId, Integer pageNum, Integer pageSize) {
//        return null;
//    }

    @Override
    public BookmarkInfoVO getBookmark(String userId, String bookId, String chapterId) {
        var bookmarkOpt = readerBookmarkMapper.selectInfoByUserIdAndBookIdAndChapterId(userId, bookId, chapterId);
        if(bookmarkOpt.isEmpty())
            throw BizException.badRequest(ExMsg.BOOKMARK_NOT_EXIST);
        return bookmarkOpt.get();
    }

}
