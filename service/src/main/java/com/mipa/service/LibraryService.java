package com.mipa.service;

import com.mipa.common.librarydto.LibraryDTO;
import com.mipa.common.librarydto.LibraryRequestDTO;
import com.mipa.convert.LibraryEntityConvert;
import com.mipa.model.LibraryEntity;
import com.mipa.repository.BookRepository;
import com.mipa.repository.LibraryRepository;
import com.mipa.repository.UserRepository;
import com.mipa.service.api.ILibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

//todo 自检下是否有很多不必要的更改（也就是本来只是更新单项，但是更新了一整行）
@Service
public class LibraryService implements ILibraryService {

    @Autowired
    LibraryRepository libraryRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    BookRepository bookRepo;

    public boolean addToLibrary(String userId, String bookId) {
        var userOpt = userRepo.findById(userId);
        if(userOpt.isPresent()){
            var bookOpt = bookRepo.findById(bookId);
            if(bookOpt.isPresent()){
                var libraryOpt = libraryRepo.findByUserIdAndBookId(userOpt.get(), bookOpt.get());
                if(libraryOpt.isEmpty()){
                    var libraryEntity = new LibraryEntity();
                    libraryEntity.setUser(userOpt.get());
                    libraryEntity.setBook(bookOpt.get());
                    libraryEntity.setAddedAt(LocalDateTime.now());
//                libraryEntity.setLastReadAt(LocalDateTime.now());//这里如果只是添加到书架但未阅读，默认最后阅读时间为当前时间
//                libraryEntity.setLastReadChapter();这两项为null
                    libraryRepo.save(libraryEntity);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeFromLibrary(String userId, String bookId) {
        var userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            var bookOpt = bookRepo.findById(bookId);
            if (bookOpt.isPresent()) {
                var libraryOpt = libraryRepo.findByUserIdAndBookId(userOpt.get(), bookOpt.get());
                if (libraryOpt.isPresent()) {
                    libraryRepo.delete(libraryOpt.get());
                    return true;
                }
            }
        }
        return false;
    }

    public LibraryDTO getFromLibrary(String userId, String bookId) {
        var userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            var bookOpt = bookRepo.findById(bookId);
            if (bookOpt.isPresent()) {
                var libraryOpt = libraryRepo.findByUserIdAndBookId(userOpt.get(), bookOpt.get());
                if (libraryOpt.isPresent()) {
                    return LibraryEntityConvert.toLibraryDTO(libraryOpt.get(), bookOpt.get(), userId);
                }
            }
        }
        return null;
    }

    public boolean updateLibrary(String userId, String bookId, LibraryRequestDTO dto) {
        var userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            var bookOpt = bookRepo.findById(bookId);
            if (bookOpt.isPresent()) {
                var libraryOpt = libraryRepo.findByUserIdAndBookId(userOpt.get(), bookOpt.get());
                if (libraryOpt.isPresent()) {
                    var libraryEntity = libraryOpt.get();
                    libraryEntity.setLastReadChapter(dto.getLastReadChapter());
                    libraryEntity.setLastReadAt(LocalDateTime.now());
                    libraryRepo.save(libraryEntity);
                    return true;
                }
            }
        }
        return false;
    }

    public Page<LibraryDTO> getUserLibrary(String userId, Integer pageNumber, Integer pageSize) {
        var userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            var pageable = PageRequest.of(pageNumber, pageSize);
            var page = libraryRepo.findByUser(userOpt.get(), pageable);
            return page.map(libraryEntity -> LibraryEntityConvert.toLibraryDTO(libraryEntity, libraryEntity.getBook(), userId));
        }
        return null;
    }
}
