package com.mipa.validate;

import com.mipa.common.Enum.VerifyResult;
import com.mipa.common.utils.TypeSafeMap;
import com.mipa.mapper.*;
import com.mipa.model.*;

import java.util.Objects;

public class VerifyRelationShip {

    private TypeSafeMap TSM;

    private VerifyResult result;

    public static VerifyRelationShip start(){
        var vr  = new VerifyRelationShip();
        vr.TSM = new TypeSafeMap();
        vr.result = VerifyResult.Success;
        return vr;
    }


    public VerifyRelationShip verifyBookAndChapter(String bookId, String chapterId, ChapterMapper mapper) {
        if (!isSucceed()) return failed();
        var chapterOpt = mapper.selectById(chapterId);
        if (chapterOpt.isEmpty()) {
            result = VerifyResult.Failed;
            return this;
        }
        var chapter = chapterOpt.get();
        if (!Objects.equals(chapter.getBookId(), bookId)) {
            result = VerifyResult.Failed;
            return this;
        }
        TSM.put(Chapter.class, chapter);
        return this;
    }

    public VerifyRelationShip verifyAuthorAndBook(String authorId, String bookId, BookMapper mapper) {
        if (!isSucceed()) return failed();
        var bookOpt = mapper.selectById(bookId);
        if (bookOpt.isEmpty()) {
            result = VerifyResult.Failed;
            return this;
        }
        var book = bookOpt.get();
        if (!Objects.equals(book.getAuthorId(), authorId)) {
            result = VerifyResult.Failed;
            return this;
        }
        TSM.put(Book.class, book);
        return this;
    }

    public VerifyRelationShip verifyChapterIdAndOrder(Integer chapterOrder, String chapterId, ChapterMapper mapper) {
        if (!isSucceed()) return failed();
        var chapter = TSM.get(Chapter.class);
        if (chapter != null && Objects.equals(chapter.getChapterOrder(), chapterOrder)) return this;

        var chapterOpt = mapper.selectById(chapterId);
        if (chapterOpt.isEmpty()) {
            result = VerifyResult.Failed;
            return this;
        }
        chapter = chapterOpt.get();
        if (!Objects.equals(chapter.getChapterOrder(), chapterOrder)) result = VerifyResult.Failed;

        return this;

    }

    /**
     * 验证这个书签是否是userid用户的
     * @param userId
     * @param bookmarkId
     * @param mapper
     * @return
     */
    public VerifyRelationShip verifyBookmarkAndUser(String userId, String bookmarkId, ReaderBookmarkMapper mapper){
        if (!isSucceed()) return failed();
        var bookmark = get(ReaderBookmark.class);
        if(bookmark == null){
            var bookmarkOpt = mapper.selectById(bookmarkId);
            if(bookmarkOpt.isEmpty())return failed();
            bookmark = bookmarkOpt.get();
        }
        if(Objects.equals(bookmark.getUserId(), userId)){
            TSM.put(ReaderBookmark.class, bookmark);
            return success();
        }
        return failed();
    }

    /**
     * 验证这个阅读记录是否是userid用户的
     * @param userId
     * @param readerProgressId
     * @param mapper
     * @return
     */
    public VerifyRelationShip verifyReaderProgressAndUser(String userId, String readerProgressId, ReaderProgressMapper mapper) {
        if (!isSucceed()) return failed();
        var readerProgress = get(ReaderProgress.class);
        if (readerProgress == null) {
            var readerProgressOpt = mapper.selectById(readerProgressId);
            if (readerProgressOpt.isEmpty()) {
                result = VerifyResult.Failed;
                return this;
            }
            readerProgress = readerProgressOpt.get();
        }
        if (Objects.equals(readerProgress.getUserId(), userId)) {
            TSM.put(ReaderProgress.class, readerProgress);
            return this;
        }
        result = VerifyResult.Failed;
        return this;
    }

    /**
     * 验证这条评论是否是yserid发送的
     * @param userId
     * @param commentId
     * @param mapper
     * @return
     */
    public VerifyRelationShip verifyCommentAndUserId(String userId, String commentId, ReaderCommentMapper mapper) {
        if (!isSucceed()) return failed();
        var comment = TSM.get(ReaderComment.class);
        if (comment == null) {
            var commentOpt = mapper.selectById(commentId);
            if (commentOpt.isEmpty())
                return failed();
            comment = commentOpt.get();
        }
        if (Objects.equals(comment.getUserId(), userId)) {
            TSM.put(ReaderComment.class, comment);
            return success();
        }
        return failed();
    }

    public boolean isSucceed(){
        return result == VerifyResult.Success;
    }


    private VerifyRelationShip success(){
        result = VerifyResult.Success;
        return this;
    }

    private VerifyRelationShip failed(){
        result = VerifyResult.Failed;
        return this;
    }

    public <T> T get(Class<T> type) {
        return TSM.get(type);
    }
}
