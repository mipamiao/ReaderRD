package com.mipa.repository;

import com.mipa.model.BookEntity;
import com.mipa.model.ChapterEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

//todo 大量的长string存储在数据库里是否会有额外的性能和空间开销，考虑设计碎片化文件的对象存储服务
public interface ChapterRepository extends CrudRepository<ChapterEntity, String> {

    @Query("SELECT c FROM ChapterEntity c WHERE c.book = :book ORDER BY c.order DESC")
    List<ChapterEntity> findByBookIdOrderByOrderAsc(@Param("book")BookEntity book);

    @Query("SELECT c FROM ChapterEntity c WHERE c.book = :book And c.order = 1")
    ChapterEntity findFirstChapterByBook(@Param("book") BookEntity book);
}
