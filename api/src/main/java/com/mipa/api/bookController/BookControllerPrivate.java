package com.mipa.api.bookController;

import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.bookdto.BookListResponseDTO;
import com.mipa.common.bookdto.BookRequestDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.repository.BookRepository;
import com.mipa.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/private/book", produces = "application/json")
public class BookControllerPrivate {
    @Autowired
    BookService bookService;

    @PreAuthorize("hasRole('WRITER")
    @PostMapping(path = "/add", consumes = "application/json")
    public ApiResponse<Boolean> addBook(@RequestBody BookRequestDTO dto,
        @AuthenticationPrincipal UserSecurity userSecurity) {
        var res = bookService.addBook(dto, userSecurity.getUserId());
        if (res) {
            return ApiResponse.success(null);
        }
        return ApiResponse.status(HttpStatus.CONFLICT, null);
    }

    @PreAuthorize("hasRole('WRITER")
    @PostMapping(path = "/update", consumes = "application/json")
    public ApiResponse<Boolean> updateBook(@RequestBody BookRequestDTO dto,
        @AuthenticationPrincipal UserSecurity userSecurity,
        @RequestParam(required = true) String bookId) {
        var res = bookService.updateBook(dto, userSecurity.getUserId(), bookId);
        if (res) {
            return ApiResponse.success(null);
        }
        return ApiResponse.status(HttpStatus.CONFLICT, null);
    }

    @PreAuthorize("hasRole('WRITER")
    @DeleteMapping(path = "/remove", consumes = "application/json")
    public ApiResponse<Boolean> deleteBook(@AuthenticationPrincipal UserSecurity userSecurity,
        @RequestParam(required = true) String bookId) {
        var res = bookService.deleteBook(bookId,userSecurity.getUserId());
        if (res) {
            return ApiResponse.success(null);
        }
        return ApiResponse.status(HttpStatus.CONFLICT, null);
    }

    @PreAuthorize("hasRole('WRITER")
    @GetMapping(path = "/my-books", produces = "application/json")
    public ApiResponse<BookListResponseDTO> getMyBooks(
        @AuthenticationPrincipal UserSecurity userSecurity,
        @RequestParam(defaultValue = "0", name = "pageNumber") Integer pageNumber,
        @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize) {

        var res = bookService.getBooksByUserId(userSecurity.getUserId(), pageNumber, pageSize);
        BookListResponseDTO dto = new BookListResponseDTO();
        dto.setBooks(res.getContent());
        dto.setPageSize(pageSize);
        dto.setPageNumber(pageNumber);
        dto.setTotal((int) res.getTotalElements());

        return ApiResponse.success(dto);
    }
}
