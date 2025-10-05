package com.mipa.service.api;

import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.BookWithTagAndAuthorNameVO;


public interface ISearchService {
    PageRecord<BookWithTagAndAuthorNameVO> searchBooks(String keyword, Integer pageNumber, Integer pageSize);
}
