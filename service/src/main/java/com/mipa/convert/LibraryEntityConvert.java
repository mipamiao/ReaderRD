package com.mipa.convert;

import com.mipa.common.librarydto.LibraryDTO;
import com.mipa.model.BookEntity;
import com.mipa.model.LibraryEntity;

public class LibraryEntityConvert {
    public static LibraryDTO toLibraryDTO(LibraryEntity library, BookEntity book, String userId) {
        if (library == null) {
            return null;
        }
        LibraryDTO dto = new LibraryDTO();
        dto.setId(library.getId());
        dto.setUserId(userId);
        dto.setBook(BookEntityConvert.toBookDTO(library.getBook()));
        dto.setLastReadChapter(library.getLastReadChapter());
        dto.setLastReadAt(library.getLastReadAt());
        dto.setAddedAt(library.getAddedAt());
        return dto;
    }
}
