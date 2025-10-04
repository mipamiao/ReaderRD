package com.mipa.common.commentdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private String bookId;
    private String chapterId;
    private String message;
    private String replyCommentId;
}
