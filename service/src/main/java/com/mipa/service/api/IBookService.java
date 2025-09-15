package com.mipa.service.api;

import com.mipa.common.bookdto.BookDTO;
import com.mipa.common.bookdto.BookRequestDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface IBookService {

  Page<BookDTO> findByPageable(int pageNumber, int pageSize);

  Page<BookDTO> findByCategory(String category, int pageNumber, int pageSize);

  Optional<BookDTO> findById(String bookId);

  Boolean addBook(BookRequestDTO bookRequestDTO, String userId);

  Boolean updateBook(BookRequestDTO bookRequestDTO, String userId, String bookId);

  Boolean deleteBook(String bookId, String userId);

  Page<BookDTO> getBooksByUserId(String userId, int pageNumber, int pageSize);
}
