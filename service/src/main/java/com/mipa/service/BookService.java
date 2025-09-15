package com.mipa.service;

import com.mipa.common.bookdto.BookDTO;
import com.mipa.common.bookdto.BookRequestDTO;
import com.mipa.convert.BookEntityConvert;
import com.mipa.convert.UserEntityConvert;
import com.mipa.model.BookEntity;
import com.mipa.model.UserEntity;
import com.mipa.repository.BookRepository;
import com.mipa.repository.UserRepository;
import com.mipa.service.api.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookService implements IBookService {


    @Autowired
    BookRepository bookRepo;

    @Autowired
    UserRepository userRepo;

    public Page<BookDTO> findByPageable(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return bookRepo.findByPageable(pageable).map(BookEntityConvert::toBookDTO);
    }

    public Page<BookDTO> findByCategory(String category, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return bookRepo.findByCategory(category, pageable).map(BookEntityConvert::toBookDTO);
    }

    public Optional<BookDTO> findById(String bookId) {
        var bookOpt = bookRepo.findById(bookId);
        return bookOpt.map(BookEntityConvert::toBookDTO);
    }


    public Boolean addBook(BookRequestDTO bookRequestDTO, String userId) {
        var userOpt = userRepo.findById(userId) ;
        if(userOpt.isPresent()){
            var user = userOpt.get();
            BookEntity bookEntity = BookEntityConvert.fromBookDTO(bookRequestDTO, user);
            BookEntity savedEntity = bookRepo.save(bookEntity);
            return true;
        }
        return false;
    }

    public Boolean updateBook(BookRequestDTO bookRequestDTO, String userId, String bookId) {
        var userOpt = userRepo.findById(userId) ;
        if(userOpt.isPresent()){
            var user = userOpt.get();
            var bookOpt = bookRepo.findById(bookId);
            if(bookOpt.isPresent()){
                var existingBook = bookOpt.get();
                if(existingBook.getAuthor() != null && existingBook.getAuthor().getUserId().equals(userId)){
                    // 仅更新允许修改的字段
                    existingBook.setTitle(bookRequestDTO.getTitle());
                    existingBook.setDescription(bookRequestDTO.getDescription());
                    existingBook.setCoverImage(bookRequestDTO.getCoverImage());
                    existingBook.setCategory(bookRequestDTO.getCategory());
                    existingBook.setTags(bookRequestDTO.getTags());
                    existingBook.setChaptersCount(bookRequestDTO.getChaptersCount());
                    existingBook.setUpdatedAt(LocalDateTime.now());
                    bookRepo.save(existingBook);
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean deleteBook(String bookId, String userId) {
        var userOpt = userRepo.findById(userId) ;
        if(userOpt.isPresent()){
            var user = userOpt.get();
            var bookOpt = bookRepo.findById(bookId);
            if(bookOpt.isPresent()){
                var existingBook = bookOpt.get();
                if(existingBook.getAuthor() != null && existingBook.getAuthor().getUserId().equals(userId)){
                    bookRepo.delete(existingBook);
                    return true;
                }
            }
        }
        return false;
    }

    public Page<BookDTO> getBooksByUserId(String userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserEntity user = UserEntityConvert.specifyUserId(userId);
        return bookRepo.findByAuthorId(user, pageable).map(BookEntityConvert::toBookDTO);
    }
}
