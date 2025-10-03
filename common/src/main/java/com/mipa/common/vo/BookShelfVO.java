package com.mipa.common.vo;

import com.mipa.common.bookdto.BookDTO;
import com.mipa.common.chapterdto.ChapterInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookShelfVO {
    private String id;
    private String userId;
    private String bookId;
    private String chapterId;
    private Integer chapterOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String coverUrl;
}
