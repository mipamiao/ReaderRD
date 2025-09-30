package com.mipa.repository;

import com.mipa.model.BookmarkEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends CrudRepository<BookmarkEntity, String> {

    @Query("SELECT bm FROM BookmarkEntity bm WHERE bm.userId = :userId and bm.bookId = :bookId and bm.chapterId = :chapterId ")
    Optional<BookmarkEntity> findByUserIdAndBookIdAndChapterId(
            @Param("userId") String userId, @Param("bookId") String bookId, @Param("chapterId") String chapterId);

    @Query("SELECT bm FROM BookmarkEntity bm WHERE bm.userId = :userId and bm.bookId = :bookId")
    List<BookmarkEntity> findByUserIdAndBookId(@Param("userId") String userId, @Param("bookId") String bookId);
}
