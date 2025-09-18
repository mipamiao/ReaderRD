package com.mipa.api.SearchController;

import com.mipa.common.bookdto.BookDTO;
import com.mipa.common.bookdto.BookListResponseDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.service.SearchService;
import com.mipa.service.api.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/public/search", produces = "application/json")
public class SearchPublicController {

    @Autowired
    private ISearchService searchService;

    @GetMapping(path = "/books")
    public ApiResponse<BookListResponseDTO> searchBooks(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0", name = "pageNumber") Integer pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize) {
        var booksPage = searchService.searchBooks(keyword, pageNumber, pageSize);
        var dto = new BookListResponseDTO();
        dto.setBooks(booksPage.getContent());
        dto.setPageSize(booksPage.getSize());
        dto.setPageNumber(booksPage.getNumber());
        dto.setTotal((int) booksPage.getTotalElements());
        return ApiResponse.success(dto);
    }
}
