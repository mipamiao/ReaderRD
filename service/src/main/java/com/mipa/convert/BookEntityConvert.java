package com.mipa.convert;

import com.mipa.common.bookdto.BookDTO;
import com.mipa.common.bookdto.BookRequestDTO;
import com.mipa.model.BookEntity;
import com.mipa.model.UserEntity;

import java.time.LocalDateTime;

public class BookEntityConvert {
    public static BookDTO toBookDTO(BookEntity bookEntity) {
        if (bookEntity == null) {
            return null;
        }
        return BookDTO.builder()
                .bookId(bookEntity.getBookId())
                .title(bookEntity.getTitle())
                .description(bookEntity.getDescription())
                .coverImage(bookEntity.getCoverImage())
                .category(bookEntity.getCategory())
                .tags(bookEntity.getTags())
                .chaptersCount(bookEntity.getChaptersCount())
                .authorName(bookEntity.getAuthor() != null ? bookEntity.getAuthor().getUserName() : null)
                .createdAt(bookEntity.getCreatedAt())
                .updatedAt(bookEntity.getUpdatedAt())
                .build();
    }

    public static BookEntity fromBookDTO(BookRequestDTO bookRequestDTO, UserEntity userEntity) {
        if (bookRequestDTO == null) {
            return null;
        }
        BookEntity bookEntity = new BookEntity();
        bookEntity.setTitle(bookRequestDTO.getTitle());
        bookEntity.setDescription(bookRequestDTO.getDescription());
        bookEntity.setCoverImage(bookRequestDTO.getCoverImage());
        bookEntity.setCategory(bookRequestDTO.getCategory());
        bookEntity.setTags(bookRequestDTO.getTags());
        bookEntity.setChaptersCount(0);
        // 注意：author需要单独处理，因为BookDTO中只有authorName
        bookEntity.setAuthor(userEntity);
        bookEntity.setCreatedAt(LocalDateTime.now());
        bookEntity.setUpdatedAt(LocalDateTime.now());
        return bookEntity;
    }
    
    public static BookEntity specifyBookId(String bookId){
        BookEntity bookEntity = new BookEntity();
        bookEntity.setBookId(bookId);
        return bookEntity;
    }
}
