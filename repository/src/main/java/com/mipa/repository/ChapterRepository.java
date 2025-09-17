package com.mipa.repository;

import com.mipa.model.BookEntity;
import com.mipa.model.ChapterEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChapterRepository extends CrudRepository<ChapterEntity, String> {

    @Query("SELECT c FROM ChapterEntity c WHERE c.book = :book ORDER BY c.order DESC")
    List<ChapterEntity> findByBookIdOrderByOrderAsc(@Param("book")BookEntity book);
}
