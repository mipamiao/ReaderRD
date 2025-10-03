package com.mipa.common.chapterdto;

import lombok.Data;

@Data
public class ChapterRequestDTO {
      private String authorId;
      private String bookId;
      private String name;
      private String content;
      private Integer chapterOrder;
}
