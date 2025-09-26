package com.mipa.common.chapterdto;

import lombok.Data;

import java.util.List;

@Data
public class ChapterInfoListDTO {
    List<ChapterInfoDTO> chapters;
    Integer total;
    Integer pageNumber;
    Integer pageSize;
}
