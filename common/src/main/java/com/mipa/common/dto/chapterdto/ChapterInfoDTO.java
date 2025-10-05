package com.mipa.common.dto.chapterdto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChapterInfoDTO {
    private String bookId;
    private String chapterId;
    private String name;
    private Integer chapterOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
