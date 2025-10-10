package com.mipa.common.dto.chapterdto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChapterRequestDTO {

      @NotBlank(message = "书籍id不能为空")
      private String bookId;

      @NotBlank(message = "章节名不能为空")
      private String name;


      @NotNull(message = "章节编号不能为空")
      @Min(value = 1, message = "章节编号最小为1")
      private Integer chapterOrder;
}
