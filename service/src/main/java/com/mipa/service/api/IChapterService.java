package com.mipa.service.api;

import com.mipa.common.chapterdto.ChapterInfoAndContentDTO;
import com.mipa.common.chapterdto.ChapterInfoDTO;
import com.mipa.common.chapterdto.ChapterRequestDTO;
import com.mipa.common.utils.PageRecord;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IChapterService {
    public ChapterInfoDTO addChapter(ChapterRequestDTO dto);
    public Boolean updateChapter(ChapterRequestDTO dto, String chapterId);
    public Boolean deleteChapter(String authorId, String bookId, String chapterId);
    public ChapterInfoDTO getChapterInfo(String bookId, String chapterId);
    public PageRecord<ChapterInfoDTO> listChapters(String bookId, Integer pageNum, Integer pageSize);
    public ChapterInfoAndContentDTO getChapterInfoAndContent( String bookId, String chapterId);
    public ChapterInfoAndContentDTO getChapterInfoAndContent(String bookId, Integer order);
    List<ChapterInfoDTO> listAllChapters(String bookId);
}
