package com.mipa.service.api;

import com.mipa.common.dto.bookdto.BookRequestDTO;
import java.util.Optional;

import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.BookWithTagAndAuthorNameVO;


public interface IBookService {

  PageRecord<BookWithTagAndAuthorNameVO> findByPageable(int pageNumber, int pageSize);

  PageRecord<BookWithTagAndAuthorNameVO> findByCategory(String category, int pageNumber, int pageSize);

  Optional<BookWithTagAndAuthorNameVO> findById(String bookId);

  Boolean addBook(BookRequestDTO bookRequestDTO, String userId);

  Boolean updateBook(BookRequestDTO bookRequestDTO, String userId, String bookId);

  Boolean deleteBook(String bookId, String userId);

  PageRecord<BookWithTagAndAuthorNameVO> getBooksByUserId(String userId, int pageNumber, int pageSize);
}
