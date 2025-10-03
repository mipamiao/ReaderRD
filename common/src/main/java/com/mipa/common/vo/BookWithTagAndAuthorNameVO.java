package com.mipa.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookWithTagAndAuthorNameVO {
    private String id;

    private String name;

    private String description;

    private String coverUrl;

    private String category;

    private List<String> tagNames;

    private Integer chapterCount;

    private String authorName;

    private String authorId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
