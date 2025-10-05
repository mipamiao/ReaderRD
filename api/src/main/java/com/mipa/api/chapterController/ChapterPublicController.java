package com.mipa.api.chapterController;

import com.mipa.common.dto.chapterdto.ChapterInfoAndContentDTO;
import com.mipa.common.dto.chapterdto.ChapterInfoDTO;
import com.mipa.common.dto.chapterdto.ChapterInfoListDTO;
import com.mipa.common.response.ApiResponse;
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

    @GetMapping(path = "get-by-order")
    public ApiResponse<ChapterInfoAndContentDTO> getChapterByOrder(
            @RequestParam(name = "bookId") String bookId,
            @RequestParam(name = "chapterOrder") Integer order
    ) {
        var result = chapterService.getChapterInfoAndContent(bookId, order);
        if (result != null) return ApiResponse.success(result);
        return ApiResponse.unauthorized(null);
    }


    @GetMapping(path = "list")
    public ApiResponse<ChapterInfoListDTO> listChapters(
            @RequestParam(name = "bookId") String bookId,
            @RequestParam(defaultValue = "0", name = "pageNumber") Integer pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize
    ) {
        var page = chapterService.listChapters(bookId, pageNumber,pageSize);
        ChapterInfoListDTO dto = new ChapterInfoListDTO();
        dto.setChapters(page.datas());
        dto.setTotal(page.total());
        dto.setPageNumber(pageNumber);
        dto.setPageSize(page.pageSize());
        return ApiResponse.success(dto);
    }

    @GetMapping(path = "list-all")
    public ApiResponse<List<ChapterInfoDTO>> listChapters(
            @RequestParam(name = "bookId") String bookId
    ) {
        var result = chapterService.listAllChapters(bookId);
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
