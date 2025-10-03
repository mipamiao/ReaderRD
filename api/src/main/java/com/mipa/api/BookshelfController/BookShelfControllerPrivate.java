package com.mipa.api.BookshelfController;


import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.librarydto.LibraryDTO;
import com.mipa.common.librarydto.LibraryRequestDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.common.vo.BookShelfVO;
import com.mipa.service.BookShelfService;
import com.mipa.service.LibraryService;
import com.mipa.service.api.IBookShelfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/private/bookshelf", produces = "application/json")
public class BookShelfControllerPrivate {

    @Autowired
    IBookShelfService bookShelfService;

    @GetMapping(path = "/list")
    public ApiResponse<List<BookShelfVO>> getUserBookshelf(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(defaultValue = "0", name = "pageNumber") Integer pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize
    ) {

        var bookshelfList = bookShelfService.getUserBookShelf(userSecurity.getUserId(), pageNumber, pageSize);
        if(bookshelfList != null){
            return ApiResponse.success(bookshelfList.datas());
        }
        return ApiResponse.status(HttpStatus.NOT_FOUND, "User not found", null);
    }

    @PostMapping(path = "/add" , consumes = "application/json" )
    public ApiResponse<Boolean> addBookshelf(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam String bookId
    ) {
        boolean result = bookShelfService.addToBookShelf(userSecurity.getUserId(), bookId);
        if (result) {
            return ApiResponse.success(true);
        } else {
            return ApiResponse.status(HttpStatus.BAD_REQUEST, "Failed to add to library", false);
        }
    }

    @PostMapping(path = "/update", consumes = "application/json" )
    public ApiResponse<Boolean> updateBookshelf(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody LibraryRequestDTO dto
    ) {
        boolean result = bookShelfService.updateBookShelf(userSecurity.getUserId(), dto.getBookId(), dto);
        if (result) {
            return ApiResponse.success(true);
        } else {
            return ApiResponse.status(HttpStatus.BAD_REQUEST, "Failed to update library", false);
        }
    }

    @DeleteMapping(path = "/remove")
    public ApiResponse<Boolean> removeBookshelf(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam String bookId
    ) {
        boolean result = bookShelfService.removeFromBookShelf(userSecurity.getUserId(), bookId);
        if (result) {
            return ApiResponse.success(true);
        } else {
            return ApiResponse.status(HttpStatus.BAD_REQUEST, "Failed to remove from library", false);
        }
    }
}
