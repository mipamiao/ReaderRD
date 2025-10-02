package com.mipa.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String id;
    private String name;
    private String description;
    private String coverUrl;
    private String category;
    private Integer chapterCount;
    private String authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

