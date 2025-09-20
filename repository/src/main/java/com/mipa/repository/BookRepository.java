package com.mipa.repository;

import com.mipa.model.BookEntity;
import com.mipa.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BookRepository extends CrudRepository<BookEntity, String> {

    @Query("SELECT b FROM BookEntity b order by b.chaptersCount desc")
    Page<BookEntity> findByPageable(Pageable pageable);

    @Query("SELECT b FROM BookEntity b WHERE b.category = :category")
    Page<BookEntity> findByCategory(@Param("category") String category, Pageable pageable);

    @Query("SELECT b FROM BookEntity b WHERE b.author = :author ORDER BY b.updatedAt desc")
    Page<BookEntity> findByAuthorId(@Param("author") UserEntity author, Pageable pageable);


    @Modifying
    @Query("UPDATE BookEntity b SET b.chaptersCount = :chaptersCount, b.updatedAt = :updatedAt WHERE b.bookId = :bookId")
    Integer updateChapterCount(
            @Param("bookId") String bookId,
            @Param("chaptersCount") Integer chaptersCount,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Modifying
    @Query("UPDATE BookEntity b SET b.updatedAt = :updatedAt WHERE b.bookId = :bookId")
    Integer updatedAt(
            @Param("bookId") String bookId,
            @Param("updatedAt") LocalDateTime updatedAt);

    @Query("SELECT b FROM BookEntity b WHERE b.title LIKE %:keyword% OR b.description LIKE %:keyword% OR b.category LIKE %:keyword%")
    Page<BookEntity> searchBooksByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("Update BookEntity b set b.coverImage = :coverImg where b.bookId = :bookId")
    Integer updateBookCoverImg(@Param("bookId") String bookId, @Param("coverImg") String coverImgUrl);
}
