package com.mipa.service.api;

import com.mipa.common.dto.chapterdto.ChapterInfoAndContentDTO;
import com.mipa.common.dto.chapterdto.ChapterInfoDTO;
import com.mipa.common.dto.chapterdto.ChapterRequestDTO;
import com.mipa.common.utils.PageRecord;


import java.util.List;

public interface IChapterService {
    ChapterInfoDTO addChapter(ChapterRequestDTO dto);

    void updateChapter(ChapterRequestDTO dto, String chapterId);

    void deleteChapter(String authorId, String bookId, String chapterId);

    ChapterInfoDTO getChapterInfo(String bookId, String chapterId);

    PageRecord<ChapterInfoDTO> listChapters(String bookId, Integer pageNum, Integer pageSize);

    ChapterInfoAndContentDTO getChapterInfoAndContent(String bookId, String chapterId);

    ChapterInfoAndContentDTO getChapterInfoAndContent(String bookId, Integer order);

    List<ChapterInfoDTO> listAllChapters(String bookId);
}
