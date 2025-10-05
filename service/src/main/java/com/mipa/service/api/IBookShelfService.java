package com.mipa.service.api;

import com.mipa.common.dto.bookshelfdto.BookShelfRequestDTO;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.BookShelfVO;

public interface IBookShelfService {
    void addToBookShelf(String userId, String bookId);
    void removeFromBookShelf(String userId, String bookId);
    BookShelfVO getFromBookShelf(String userId, String bookId);
    void updateBookShelf(String userId, String bookId, BookShelfRequestDTO dto);
    PageRecord<BookShelfVO> getUserBookShelf(String userId, Integer pageNumber, Integer pageSize);
}
