package com.mipa.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReaderProgressVO {
    private String bookId;
    private String chapterId;
    private Integer chapterOrder;

    private String bookName;
    private String coverUrl;


}
