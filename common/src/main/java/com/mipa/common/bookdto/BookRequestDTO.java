package com.mipa.common.bookdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookRequestDTO {


    private String title;

    private String description;

    private String coverImage;

    private String category;

    private List<String> tags;

    private Integer chaptersCount;

}

