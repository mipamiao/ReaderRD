package com.mipa.api.bookController;

import com.mipa.common.dto.bookdto.BookListResponseDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.BookWithTagAndAuthorNameVO;
import com.mipa.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


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
        PageRecord<BookWithTagAndAuthorNameVO> page = null;

        if (category== null)
            page = bookService.findByPageable(pageNumber, pageSize);
        else page = bookService.findByCategory(category, pageNumber, pageSize);

        BookListResponseDTO responseDTO = new BookListResponseDTO();
        responseDTO.setBooks(page.getDatas());
        responseDTO.setTotal(page.getTotal());
        responseDTO.setPageNumber(pageNumber);
        responseDTO.setPageSize(pageSize);
        return ApiResponse.success(responseDTO);
    }

    @GetMapping(path = "/get")
    public ApiResponse<BookWithTagAndAuthorNameVO> getBookById(
            @RequestParam(required = true) String bookId
    ) {
        return ApiResponse.success(bookService.findById(bookId));
    }
}
