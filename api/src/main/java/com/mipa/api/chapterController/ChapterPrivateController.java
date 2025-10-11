package com.mipa.api.chapterController;

import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.dto.chapterdto.ChapterInfoDTO;
import com.mipa.common.dto.chapterdto.ChapterInfoListDTO;
import com.mipa.common.dto.chapterdto.ChapterRequestDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.service.api.IChapterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/private/chapter", produces = "application/json")
public class ChapterPrivateController {

    @Autowired
    private IChapterService chapterService;

    @PreAuthorize("hasRole('WRITER')")
    @PostMapping(path = "/add", consumes = "application/json")
    public ApiResponse<ChapterInfoDTO> addChapter(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody @Valid ChapterRequestDTO dto
    ) {
        var result = chapterService.addChapter(dto, userSecurity.getUserId());
        if (result != null) return ApiResponse.success(result);
        return ApiResponse.unauthorized(null);
    }

    @PreAuthorize("hasRole('WRITER')")
    @PostMapping(path = "/update", consumes = "application/json")
    public ApiResponse<Boolean> updateChapter(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody @Valid ChapterRequestDTO dto,
            @RequestParam(name = "chapterId") String chapterId
    ) {
        chapterService.updateChapter(dto, userSecurity.getUserId(), chapterId);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('WRITER')")
    @DeleteMapping(path = "/remove")
    public ApiResponse<Boolean> deleteChapter(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "chapterId") String chapterId
    ) {
        chapterService.deleteChapter(userSecurity.getUserId(), chapterId);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('WRITER')")
    @DeleteMapping(path = "/change-publish-state")
    public ApiResponse<Boolean> deleteChapter(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "chapterId") String chapterId,
            @RequestParam(name = "publishState") Boolean publishState
    ) {
        chapterService.updatePublishState(userSecurity.getUserId(), chapterId, publishState);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('WRITER')")
    @GetMapping(path = "get")
    public ApiResponse<ChapterInfoDTO> getChapter(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "chapterId") String chapterId
    ) {
        var result = chapterService.getWholeChapterInfo(userSecurity.getUserId(), chapterId);
        if (result != null) return ApiResponse.success(result);
        return ApiResponse.unauthorized(null);
    }

    @GetMapping(path = "get-by-order")
    public ApiResponse<ChapterInfoDTO> getChapterByOrder(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "bookId") String bookId,
            @RequestParam(name = "chapterOrder") Integer order
    ) {
        var result = chapterService.getWholeChapterInfo(userSecurity.getUserId(), bookId, order);
        if (result != null) return ApiResponse.success(result);
        return ApiResponse.unauthorized(null);
    }


    @GetMapping(path = "list")
    public ApiResponse<ChapterInfoListDTO> listChapters(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "bookId") String bookId,
            @RequestParam(defaultValue = "0", name = "pageNumber") Integer pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize
    ) {
        var page = chapterService.listWholeChapters(userSecurity.getUserId(), bookId, pageNumber,pageSize);
        ChapterInfoListDTO dto = new ChapterInfoListDTO();
        dto.setChapters(page.getDatas());
        dto.setTotal(page.getTotal());
        dto.setPageNumber(pageNumber);
        dto.setPageSize(page.getPageSize());
        return ApiResponse.success(dto);
    }

    @GetMapping(path = "list-all")
    public ApiResponse<List<ChapterInfoDTO>> listChapters(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "bookId") String bookId
    ) {
        var result = chapterService.listAllWholeChapters(userSecurity.getUserId(), bookId);
        if (result != null) return ApiResponse.success(result);
        return ApiResponse.unauthorized(null);
    }

    @GetMapping(path = "info")
    public ApiResponse<ChapterInfoDTO> getChapterInfo(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "chapterId") String chapterId
    ) {
        var result = chapterService.getWholeChapterInfo(userSecurity.getUserId(), chapterId);
        if (result != null) return ApiResponse.success(result);
        return ApiResponse.unauthorized(null);
    }

    @PreAuthorize("hasRole('WRITER')")
    @GetMapping(path = "clear-content")
    public ApiResponse<String> clearContent(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "chapterId") String chapterId
    ){
        chapterService.clearChapterContent(chapterId);
        return ApiResponse.success(null);
    }

    @GetMapping(path = "write-in-web")
    public ApiResponse<String> writeInWeb(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "chapterId") String chapterId
    ){
        return ApiResponse.success(null);
    }

}
