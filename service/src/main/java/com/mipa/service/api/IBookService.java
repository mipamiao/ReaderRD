package com.mipa.service.api;

import com.mipa.common.dto.bookdto.BookRequestDTO;
import java.util.Optional;

import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.BookWithTagAndAuthorNameVO;
import org.springframework.web.multipart.MultipartFile;


public interface IBookService {

  PageRecord<BookWithTagAndAuthorNameVO> findByPageable(int pageNumber, int pageSize);

  PageRecord<BookWithTagAndAuthorNameVO> findByCategory(String category, int pageNumber, int pageSize);

  BookWithTagAndAuthorNameVO findById(String bookId);

  Boolean addBook(BookRequestDTO bookRequestDTO, String userId);

  void updateBook(String bookId, BookRequestDTO bookRequestDTO, String userId);

  void deleteBook(String bookId, String userId);

  PageRecord<BookWithTagAndAuthorNameVO> getBooksByUserId(String userId, int pageNumber, int pageSize);

  String updateCoverImage(MultipartFile file, String bookId, String userId);
}
