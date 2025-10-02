package com.mipa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderComment {
    private String id;
    private String userId;
    private String bookId;
    private String chapterId;
    private String message;
    private String replyCommentId;
    private LocalDateTime createdAt;
}
