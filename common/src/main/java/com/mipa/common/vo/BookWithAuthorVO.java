package com.mipa.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookWithAuthorVO {
    private String id;

    private String name;

    private String description;

    private String coverUrl;

    private String category;

    private Integer chapterCount;

    private String authorName;

    private String authorId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
