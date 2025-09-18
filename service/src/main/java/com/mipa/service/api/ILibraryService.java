package com.mipa.service.api;

import com.mipa.common.librarydto.LibraryDTO;
import com.mipa.common.librarydto.LibraryRequestDTO;
import org.springframework.data.domain.Page;

public interface ILibraryService {
    public boolean addToLibrary(String userId, String bookId);
    public boolean removeFromLibrary(String userId, String bookId);
    public LibraryDTO getFromLibrary(String userId, String bookId);
    public boolean updateLibrary(String userId, String bookId, LibraryRequestDTO dto);
    public Page<LibraryDTO> getUserLibrary(String userId, Integer pageNumber, Integer pageSize);
}
