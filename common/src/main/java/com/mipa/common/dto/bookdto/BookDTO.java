package com.mipa.common.dto.bookdto;

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
    private String id;

    private String name;

    private String description;

    private String coverUrl;

    private String category;

    private Integer chapterCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
