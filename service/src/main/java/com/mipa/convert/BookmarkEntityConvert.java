package com.mipa.convert;

import com.mipa.common.bookmarkdto.BookmarkInfoDTO;
import com.mipa.common.bookmarkdto.BookmarkRequestDTO;
import com.mipa.model.BookmarkEntity;

public class BookmarkEntityConvert {

    public static BookmarkEntity fromBookmarkRequestDTO(BookmarkRequestDTO dto){
        BookmarkEntity entity = new BookmarkEntity();
        entity.setBookId(dto.getBookId());
        entity.setChapterId(dto.getChapterId());
        entity.setChapterTitle(dto.getChapterTitle());
        entity.setNote(dto.getNote());
        entity.setOrder(dto.getOrder());
        return entity;
    }

    public static BookmarkInfoDTO toBookmarkInfoDTO(BookmarkEntity entity){
        BookmarkInfoDTO dto = new BookmarkInfoDTO();
        dto.setId(entity.getId());
        dto.setNote(entity.getNote());
        dto.setBookId(entity.getBookId());
        dto.setChapterId(entity.getChapterId());
        dto.setChapterTitle(entity.getChapterTitle());
        dto.setOrder(entity.getOrder());
        dto.setCreatedAt(entity.getCreateTime());
        dto.setUpdatedAt(entity.getUpdateTime());
        return dto;
    }
}
