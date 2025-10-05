package com.mipa.common.dto.bookdto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookRequestDTO {

    @NotBlank(message = "书籍名不能为空")
    private String name;

    private String description;

    private String coverUrl;

    @NotBlank(message = "类别不能为空")
    private String category;

    private List<String> tags;

    private Integer chapterCount;

}

