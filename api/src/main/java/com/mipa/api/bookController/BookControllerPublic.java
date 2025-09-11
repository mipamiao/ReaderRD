package com.mipa.api.bookController;

import com.mipa.common.bookdto.BookDTO;
import com.mipa.common.bookdto.BookListResponseDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.repository.BookRepository;
import com.mipa.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


@RestController
@RequestMapping(path = "/api/public/book", produces = "application/json")
public class BookControllerPublic {

    @Autowired
    BookService bookService;

    @GetMapping(path = "/list")
    public ApiResponse<BookListResponseDTO> getBooks(
            @RequestParam(defaultValue = "0", name = "pageNumber") Integer pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize,
            @RequestParam(required = false) String category
    ) {
        Page<BookDTO> page = null;

        if (category== null)
            page = bookService.findByPageable(pageNumber, pageSize);
        else page = bookService.findByCategory(category, pageNumber, pageSize);

        BookListResponseDTO responseDTO = new BookListResponseDTO();
        responseDTO.setBooks(page.getContent());
        responseDTO.setTotal((int) page.getTotalElements());
        responseDTO.setPageNumber(pageNumber);
        responseDTO.setPageSize(pageSize);
        return ApiResponse.success(responseDTO);
    }

    @GetMapping(path = "/get")
    public ApiResponse<BookDTO> getBookById(
            @RequestParam(required = true) String bookId
    ) {
        var bookOpt = bookService.findById(bookId);
        if (bookOpt.isPresent()) {
            return ApiResponse.success(bookOpt.get());
        } else {
            return ApiResponse.status(HttpStatus.NOT_FOUND, "Book not found", null);
        }
    }
}
