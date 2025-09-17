package com.mipa.common.chapterdto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChapterInfoDTO {
    private String authorId;
    private String bookId;
    private String chapterId;
    private String title;
    private Integer order;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
