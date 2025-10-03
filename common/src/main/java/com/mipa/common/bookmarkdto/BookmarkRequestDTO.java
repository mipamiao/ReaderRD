package com.mipa.common.bookmarkdto;

import lombok.Data;

@Data
public class BookmarkRequestDTO {
    private String bookId;
    private String chapterId;
    private Integer chapterOrder;
    private String chapterTitle;
    private String note;
}
