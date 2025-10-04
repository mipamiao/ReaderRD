package com.mipa.common.dto.chapterdto;

import lombok.Data;

import java.util.List;

@Data
public class ChapterInfoListDTO {
    List<ChapterInfoDTO> chapters;
    Long total;
    Integer pageNumber;
    Integer pageSize;
}
