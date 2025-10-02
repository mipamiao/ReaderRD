package com.mipa.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    private String id;
    private String bookId;
    private String name;
    private String content;
    private Integer chapterOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

