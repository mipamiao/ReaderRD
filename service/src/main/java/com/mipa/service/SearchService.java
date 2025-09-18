package com.mipa.service;

import com.mipa.common.bookdto.BookDTO;
import com.mipa.convert.BookEntityConvert;
import com.mipa.repository.BookRepository;
import com.mipa.service.api.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SearchService implements ISearchService {
    @Autowired
    BookRepository bookRepo;

    @Override
    public  Page<BookDTO>  searchBooks(String keyword, Integer pageNumber, Integer pageSize) {
        var pageable = PageRequest.of(pageNumber, pageSize);
        var booksPage = bookRepo.searchBooksByKeyword(keyword, pageable);
        return bookRepo.searchBooksByKeyword(keyword, pageable).map(BookEntityConvert::toBookDTO);
    }
}
