package com.mipa.common.readprogressDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReaderProgressDTO {
    private String userId;
    private String bookId;
    private String chapterId;
    private Integer chapterOrder;
}
