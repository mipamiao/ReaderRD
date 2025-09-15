package com.mipa.api.chapterController;

import com.mipa.common.chapterdto.ChapterInfoAndContentDTO;
import com.mipa.common.chapterdto.ChapterInfoDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.service.ChapterService;
import com.mipa.service.api.IChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/public/chapter", produces = "application/json")
public class ChapterPublicController {
    @Autowired
    private IChapterService chapterService;

    @GetMapping(path = "get")
    public ApiResponse<ChapterInfoAndContentDTO> getChapter(
            @RequestParam(name = "bookId") String bookId,
            @RequestParam(name = "chapterId") String chapterId
    ) {
        var result = chapterService.getChapterInfoAndContent(bookId, chapterId);
        if (result != null) return ApiResponse.success(result);
        return ApiResponse.unauthorized(null);
    }


    @GetMapping(path = "list")
    public ApiResponse<List<ChapterInfoDTO>> listChapters(
            @RequestParam(name = "bookId") String bookId
    ) {
        var result = chapterService.listChapters(bookId);
        if (result != null) return ApiResponse.success(result);
        return ApiResponse.unauthorized(null);
    }

    @GetMapping(path = "info")
    public ApiResponse<ChapterInfoDTO> getChapterInfo(
            @RequestParam(name = "bookId") String bookId,
            @RequestParam(name = "chapterId") String chapterId
    ) {
        var result = chapterService.getChapterInfo(bookId, chapterId);
        if (result != null) return ApiResponse.success(result);
        return ApiResponse.unauthorized(null);
    }



}
