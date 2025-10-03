package com.mipa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderBookmark {
    private String id;
    private String userId;
    private String bookId;
    private String chapterId;
    private String note;
    private Integer chapterOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
