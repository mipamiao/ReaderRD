package com.mipa.api.SearchController;

import com.mipa.common.dto.bookdto.BookListResponseDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.BookWithTagAndAuthorNameVO;
import com.mipa.service.api.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/public/search", produces = "application/json")
public class SearchPublicController {

    @Autowired
    private ISearchService searchService;

    @GetMapping(path = "/books")
    public ApiResponse<PageRecord<BookWithTagAndAuthorNameVO>> searchBooks(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0", name = "pageNumber") Integer pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize) {
        var booksPage = searchService.searchBooks(keyword, pageNumber, pageSize);
        return ApiResponse.success(booksPage);
    }
}
