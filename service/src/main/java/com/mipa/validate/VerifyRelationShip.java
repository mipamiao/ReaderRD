package com.mipa.validate;

import com.mipa.common.Enum.VerifyResult;
import com.mipa.common.utils.TypeSafeMap;
import com.mipa.mapper.BookMapper;
import com.mipa.mapper.ChapterMapper;
import com.mipa.mapper.ReaderProgressMapper;
import com.mipa.model.Book;
import com.mipa.model.Chapter;
import com.mipa.model.ReaderProgress;

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
        if (result == VerifyResult.Failed) return this;
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
        if (result == VerifyResult.Failed) return this;
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
        if (result == VerifyResult.Failed) return this;
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
     * 验证这个阅读记录是否是userid用户的
     * @param userId
     * @param readerProgressId
     * @param mapper
     * @return
     */
    public VerifyRelationShip verifyReaderProgressAndUser(String userId, String readerProgressId, ReaderProgressMapper mapper) {
        if (result == VerifyResult.Failed) return this;
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

    public boolean isSucceed(){
        return result == VerifyResult.Success;
    }

    public <T> T get(Class<T> type) {
        return TSM.get(type);
    }
}
