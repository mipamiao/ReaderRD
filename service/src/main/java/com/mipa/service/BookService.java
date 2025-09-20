package com.mipa.service;

import com.mipa.common.bookdto.BookDTO;
import com.mipa.common.bookdto.BookRequestDTO;
import com.mipa.common.configuration.MyConfiguration;
import com.mipa.convert.BookEntityConvert;
import com.mipa.convert.UserEntityConvert;
import com.mipa.model.BookEntity;
import com.mipa.model.UserEntity;
import com.mipa.repository.BookRepository;
import com.mipa.repository.UserRepository;
import com.mipa.service.api.IBookService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookService implements IBookService {


    @Autowired
    BookRepository bookRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    FileService fileService;

    @Autowired
    MyConfiguration config;

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

    //todo 事务失败的回滚
    @Transactional
    public String updateCoverImage(MultipartFile file, String bookId, String userId) {
        if (file.isEmpty()) return null;

        var userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) return null;
        var user = userOpt.get();

        var bookOpt = bookRepo.findById(bookId);
        if (bookOpt.isEmpty()) return null;
        var book = bookOpt.get();

        if (book.getAuthor() != null && book.getAuthor().getUserId().equals(userId)) {//todo n+1

            fileService.createDirIfNotExist(config.bookCoverImgsDstDir);
            String newFilename = fileService.generateUniqueFileName(
                    file.getOriginalFilename(), bookId
            );

            Path path = Paths.get(fileService.combinePath(config.bookCoverImgsDstDir, newFilename));
            if (!fileService.saveSmall(file, path)) return null;

            var resultUrl = fileService.combinePath(config.dataNetHost, config.bookCoverImgsSrcDir, newFilename);
            if (book.getCoverImage() != null) {
                var oldCoverPath = book.getCoverImage().replace(fileService.combinePath(
                        config.dataNetHost, config.bookCoverImgsSrcDir), config.bookCoverImgsDstDir);
                fileService.deleteSmall(oldCoverPath);
            }
            if (bookRepo.updateBookCoverImg(bookId, resultUrl) == 1) return resultUrl;
        }
        return null;
    }
}
