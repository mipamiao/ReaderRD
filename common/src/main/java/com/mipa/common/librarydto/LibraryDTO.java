package com.mipa.common.librarydto;

import com.mipa.common.bookdto.BookDTO;
import com.mipa.common.chapterdto.ChapterInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibraryDTO {

    private String id;

    private String userId;

    private BookDTO book;

    private ChapterInfoDTO lastReadChapter;

    private LocalDateTime lastReadAt;

    private LocalDateTime addedAt = LocalDateTime.now();
}