package com.mipa.service.api;

import com.mipa.common.dto.chapterdto.ChapterInfoDTO;
import com.mipa.common.dto.chapterdto.ChapterRequestDTO;
import com.mipa.common.utils.PageRecord;


import java.util.List;

public interface IChapterService {
    ChapterInfoDTO addChapter(ChapterRequestDTO dto, String authorId);

    void updateChapter(ChapterRequestDTO dto, String authorId, String chapterId);

    void deleteChapter(String authorId, String chapterId);

    ChapterInfoDTO getPublishedChapterInfo(String chapterId);

    ChapterInfoDTO getWholeChapterInfo(String authorId, String chapterId);

    ChapterInfoDTO getPublishedChapterInfo(String bookId, Integer chapterOrder);

    ChapterInfoDTO getWholeChapterInfo(String authorId, String bookId, Integer chapterOrder);

    PageRecord<ChapterInfoDTO> listWholeChapters(String authorId, String bookId, Integer pageNum, Integer pageSize);

    PageRecord<ChapterInfoDTO> listPublishedChapters(String bookId, Integer pageNum, Integer pageSize);

    List<ChapterInfoDTO> listAllPublishedChapters(String bookId);

    ChapterInfoDTO copyChapter(String authorId, String chapterId);

    void updatePublishState(String authorId, String chapterId, Boolean publishState);

    List<ChapterInfoDTO> listAllWholeChapters(String authorId, String bookId);
}
