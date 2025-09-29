package com.mipa.repository;

import com.mipa.model.BookEntity;
import com.mipa.model.ChapterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//todo 大量的长string存储在数据库里是否会有额外的性能和空间开销，考虑设计碎片化文件的对象存储服务
public interface ChapterRepository extends CrudRepository<ChapterEntity, String> {

    @Query("SELECT c FROM ChapterEntity c WHERE c.book = :book ORDER BY c.order DESC")
    Page<ChapterEntity> findByBookOrderByOrderDesc(@Param("book")BookEntity book, Pageable pageable);

    @Query("SELECT c FROM ChapterEntity c WHERE c.book = :book And c.order = 0")
    ChapterEntity findFirstChapterByBook(@Param("book") BookEntity book);

    @Query("SELECT c FROM ChapterEntity c WHERE c.book = :book AND c.order = :chapterOrder")
    Optional<ChapterEntity> findChapterByBookAndOrder(
            @Param("book") BookEntity book,
            @Param("chapterOrder") Integer order  // 避免使用关键字
    );

    @Query("SELECT c FROM ChapterEntity c WHERE c.book = :book ORDER BY c.order DESC")
    List<ChapterEntity> findAllByBookOrderByOrderDesc(@Param("book")BookEntity book);

}
