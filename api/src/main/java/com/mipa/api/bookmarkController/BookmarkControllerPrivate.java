package com.mipa.api.bookmarkController;

import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.vo.BookmarkInfoVO;
import com.mipa.common.dto.bookmarkdto.BookmarkRequestDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.service.api.IBookmarkService;
import jakarta.validation.Valid;
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
    public ApiResponse<BookmarkInfoVO> addBookmark(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody @Valid BookmarkRequestDTO dto
    ) {
        var res = bookmarkService.addBookmark(dto, userSecurity.getUserId());
        if (res != null)
            return ApiResponse.success(res);
        return ApiResponse.unauthorized(null);
    }

    @PostMapping(path = "update", consumes = "application/json")
    public ApiResponse<Boolean> updateBookmark(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody @Valid BookmarkRequestDTO dto,
            @RequestParam(name = "bookmarkId") String bookmarkId
    ) {
        bookmarkService.updateBookmark(dto, bookmarkId, userSecurity.getUserId());
        return ApiResponse.success(null);
    }

    @GetMapping(path = "get")
    public ApiResponse<BookmarkInfoVO> getBookmark(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(name = "bookId") String bookId,
            @RequestParam(name = "chapterId") String chapterId
    ){
        bookmarkService.getBookmark(userSecurity.getUserId() , bookId, chapterId);
        return ApiResponse.unauthorized(null);
    }

    @GetMapping(path = "list-all")
    public ApiResponse<List<BookmarkInfoVO>> listAllBookmark(
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
        bookmarkService.delBookmark(bookmarkId, userSecurity.getUserId());
        return ApiResponse.success(null);
    }
}
