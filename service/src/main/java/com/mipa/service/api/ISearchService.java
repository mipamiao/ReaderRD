package com.mipa.service.api;

import com.mipa.common.bookdto.BookDTO;
import org.springframework.data.domain.Page;

public interface ISearchService {
    Page<BookDTO> searchBooks(String keyword, Integer pageNumber, Integer pageSize);
}
