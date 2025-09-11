package com.mipa.repository;

import com.mipa.model.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends CrudRepository<BookEntity, String> {

    @Query("SELECT b FROM BookEntity b")
    Page<BookEntity> findByPageable(Pageable pageable);

    @Query("SELECT b FROM BookEntity b WHERE b.category = :category")
    Page<BookEntity> findByCategory(@Param("category") String category, Pageable pageable);
}
