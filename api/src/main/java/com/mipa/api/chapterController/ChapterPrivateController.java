package com.mipa.api.chapterController;

import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.chapterdto.ChapterInfoDTO;
import com.mipa.common.chapterdto.ChapterRequestDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.service.ChapterService;
import com.mipa.service.UserService;
import com.mipa.service.api.IChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/private/chapter", produces = "application/json")
public class ChapterPrivateController {

    @Autowired
    private IChapterService chapterService;

    @PreAuthorize("hasRole('WRITER')")
    @PostMapping(path = "/add", consumes = "application/json")
    public ApiResponse<ChapterInfoDTO> addChapter(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody ChapterRequestDTO dto
    ) {
        dto.setAuthorId(userSecurity.getUserId());
        var result = chapterService.addChapter(dto);
        if (result != null) return ApiResponse.success(result);
        return ApiResponse.unauthorized(null);
    }

    @PreAuthorize("hasRole('WRITER')")
    @PostMapping(path = "/update", consumes = "application/json")
    public ApiResponse<Boolean> updateChapter(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody ChapterRequestDTO dto,
            @RequestParam(name = "chapterId") String chapterId
    ) {
        dto.setAuthorId(userSecurity.getUserId());
        var result = chapterService.updateChapter(dto, chapterId);
        if (result) return ApiResponse.success(null);
        return ApiResponse.unauthorized(null);
    }

    @PreAuthorize("hasRole('WRITER')")
    @DeleteMapping(path = "/remove")
    public ApiResponse<Boolean> deleteChapter(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "bookId") String bookId,
            @RequestParam(name = "chapterId") String chapterId
    ) {
        var result = chapterService.deleteChapter(userSecurity.getUserId(), bookId, chapterId);
        if (result) return ApiResponse.success(null);
        return ApiResponse.unauthorized(null);
    }


}
