package com.mipa.service.api;

import com.mipa.common.chapterdto.ChapterInfoAndContentDTO;
import com.mipa.common.chapterdto.ChapterInfoDTO;
import com.mipa.common.chapterdto.ChapterRequestDTO;

import java.util.List;

public interface IChapterService {
    public ChapterInfoDTO addChapter(ChapterRequestDTO dto);
    public Boolean updateChapter(ChapterRequestDTO dto, String chapterId);
    public Boolean deleteChapter(String authorId, String bookId, String chapterId);
    public ChapterInfoDTO getChapterInfo(String bookId, String chapterId);
    public List<ChapterInfoDTO> listChapters(String bookId);
    public ChapterInfoAndContentDTO getChapterInfoAndContent( String bookId, String chapterId);
}
