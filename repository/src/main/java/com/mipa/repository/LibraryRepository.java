package com.mipa.repository;

import com.mipa.model.BookEntity;
import com.mipa.model.LibraryEntity;
import com.mipa.model.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LibraryRepository extends CrudRepository<LibraryEntity, String> {

    @Query("SELECT l FROM LibraryEntity l WHERE l.user = :user AND l.book = :book")
    Optional<LibraryEntity> findByUserIdAndBookId(@Param("user") UserEntity user, @Param("book") BookEntity book);

    @Query("SELECT l FROM LibraryEntity l WHERE l.user = :user ORDER BY l.addedAt DESC")
    Page<LibraryEntity> findByUser(@Param("user") UserEntity user, Pageable pageable);
}
