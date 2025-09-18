package com.mipa.api.libraryController;

import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.librarydto.LibraryDTO;
import com.mipa.common.librarydto.LibraryRequestDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/api/private/library", produces = "application/json")
public class LibraryPrivateController {
    @Autowired
    LibraryService libraryService;

    @GetMapping(path = "/list")
    public ApiResponse<List<LibraryDTO>> getUserLibrary(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(defaultValue = "0", name = "pageNumber") Integer pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize
    ) {
        var libraryList = libraryService.getUserLibrary(userSecurity.getUserId(), pageNumber, pageSize);
        if (libraryList != null) {
            return ApiResponse.success(libraryList.getContent());

        }
        return ApiResponse.status(HttpStatus.NOT_FOUND, "User not found", null);
    }

    @PostMapping(path = "/add" , consumes = "application/json" )
    public ApiResponse<Boolean> addLibrary(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam String bookId
    ) {
        boolean result = libraryService.addToLibrary(userSecurity.getUserId(), bookId);
        if (result) {
            return ApiResponse.success(true);
        } else {
            return ApiResponse.status(HttpStatus.BAD_REQUEST, "Failed to add to library", false);
        }
    }

    @PostMapping(path = "/update", consumes = "application/json" )
    public ApiResponse<Boolean> updateLibrary(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody LibraryRequestDTO dto
    ) {
        boolean result = libraryService.updateLibrary(userSecurity.getUserId(), dto.getBookId(), dto);
        if (result) {
            return ApiResponse.success(true);
        } else {
            return ApiResponse.status(HttpStatus.BAD_REQUEST, "Failed to update library", false);
        }
    }

    @DeleteMapping(path = "/remove")
    public ApiResponse<Boolean> removeLibrary(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam String bookId
    ) {
        boolean result = libraryService.removeFromLibrary(userSecurity.getUserId(), bookId);
        if (result) {
            return ApiResponse.success(true);
        } else {
            return ApiResponse.status(HttpStatus.BAD_REQUEST, "Failed to remove from library", false);
        }
    }
}
