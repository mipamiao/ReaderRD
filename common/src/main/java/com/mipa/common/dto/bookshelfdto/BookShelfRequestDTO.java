package com.mipa.common.dto.bookshelfdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookShelfRequestDTO {

    private String id;

    @NotBlank(message = "用户id不能为空")
    private String userId;

    @NotBlank(message = "书籍id不能为空")
    private String bookId;

    @NotBlank(message = "章节id不能为空")
    private String chapterId;

    @NotNull(message = "章节编号不能为空")
    @Min(value = 1, message = "章节顺序最小为1")
    private Integer chapterOrder;
}
