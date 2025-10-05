package com.mipa.common.dto.bookmarkdto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookmarkInfoDTO {
    private String id;
    private String bookId;
    private String chapterId;
    private String chapterTitle;
    private Integer chapterOrder;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
