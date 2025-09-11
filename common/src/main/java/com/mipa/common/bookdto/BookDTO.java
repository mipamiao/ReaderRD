package com.mipa.common.bookdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private String bookId;

    private String title;

    private String description;

    private String coverImage;

    private String category;

    private List<String> tags;

    private Integer chaptersCount;

    private String authorName; // 通常DTO中只存储作者ID而不是整个作者对象

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
