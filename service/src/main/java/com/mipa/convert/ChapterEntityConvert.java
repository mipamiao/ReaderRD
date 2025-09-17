package com.mipa.convert;

import com.mipa.common.chapterdto.ChapterInfoAndContentDTO;
import com.mipa.common.chapterdto.ChapterInfoDTO;
import com.mipa.common.chapterdto.ChapterRequestDTO;
import com.mipa.model.ChapterEntity;

public class ChapterEntityConvert {
    public static ChapterEntity fromChapterRequestDTO(ChapterRequestDTO dto) {
        ChapterEntity entity = new ChapterEntity();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setOrder(dto.getOrder());
        return entity;
    }

    public static ChapterInfoDTO toChapterInfoDTO(ChapterEntity entity) {
        ChapterInfoDTO dto = new ChapterInfoDTO();
        dto.setAuthorId(entity.getBook().getAuthor().getUserId());
        dto.setBookId(entity.getBook().getBookId());
        dto.setChapterId(entity.getChapterId());
        dto.setTitle(entity.getTitle());
        dto.setOrder(entity.getOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    //todo 这里string很可能是拷贝而不是直接引用，内容过多会有性能问题
    public static ChapterInfoAndContentDTO toChapterInfoAndContentDTO(ChapterEntity chapter) {
        ChapterInfoAndContentDTO dto = new ChapterInfoAndContentDTO();
        dto.setChapterInfoDTO(toChapterInfoDTO(chapter));
        dto.setContent(chapter.getContent());
        return  dto;
    }

}
