package com.mipa.common.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookmarkInfoVO {
    private String id;
    private String bookId;
    private String chapterId;
    private String chapterTitle;
    private Integer chapterOrder;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
