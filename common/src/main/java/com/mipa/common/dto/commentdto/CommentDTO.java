package com.mipa.common.dto.commentdto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    @NotBlank(message = "书籍id不能为空")
    private String bookId;

    @NotBlank(message = "章节id不能为空")
    private String chapterId;

    @NotBlank(message = "评论不能为空")
    private String message;
    private String replyCommentId;
}
