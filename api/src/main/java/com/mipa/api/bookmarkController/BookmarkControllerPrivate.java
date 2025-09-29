package com.mipa.api.bookmarkController;

import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.bookmarkdto.BookmarkInfoDTO;
import com.mipa.common.bookmarkdto.BookmarkRequestDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.service.api.IBookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/private/bookmark", produces = "application/json")
public class BookmarkControllerPrivate {

    @Autowired
    IBookmarkService bookmarkService;

    @PostMapping(path = "add", consumes = "application/json")
    public ApiResponse<BookmarkInfoDTO> addBookmark(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody BookmarkRequestDTO dto
    ) {
        var res = bookmarkService.addBookmark(dto, userSecurity.getUserId());
        if (res != null)
            return ApiResponse.success(res);
        return ApiResponse.unauthorized(null);
    }

    @PostMapping(path = "update", consumes = "application/json")
    public ApiResponse<Boolean> updateBookmark(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody BookmarkRequestDTO dto,
            @RequestParam(name = "bookmarkId") String bookmarkId
    ) {
        var res = bookmarkService.updateBookmark(dto, bookmarkId, userSecurity.getUserId());
        if (res)
            return ApiResponse.success(res);
        return ApiResponse.unauthorized(null);
    }

    @GetMapping(path = "list-all")
    public ApiResponse<List<BookmarkInfoDTO>> listAllBookmark(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "bookId") String bookId
    ) {
        var res = bookmarkService.listAllBookmark(userSecurity.getUserId(), bookId);
        if (res != null)
            return ApiResponse.success(res);
        return ApiResponse.unauthorized(null);
    }

    @DeleteMapping(path = "del")
    public ApiResponse<Boolean> delBookmark(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "bookmarkId") String bookmarkId
    ) {
        var res = bookmarkService.delBookmark(bookmarkId, userSecurity.getUserId());
        if (res)
            return ApiResponse.success(res);
        return ApiResponse.unauthorized(null);
    }
}
