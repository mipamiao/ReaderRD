package com.mipa.service.api;

import com.mipa.common.librarydto.LibraryRequestDTO;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.BookShelfVO;

public interface IBookShelfService {
    boolean addToBookShelf(String userId, String bookId);
    boolean removeFromBookShelf(String userId, String bookId);
    BookShelfVO getFromBookShelf(String userId, String bookId);
    boolean updateBookShelf(String userId, String bookId, LibraryRequestDTO dto);
    PageRecord<BookShelfVO> getUserBookShelf(String userId, Integer pageNumber, Integer pageSize);
}
