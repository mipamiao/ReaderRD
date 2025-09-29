package com.mipa.service.api;

import com.mipa.common.bookmarkdto.BookmarkInfoDTO;
import com.mipa.common.bookmarkdto.BookmarkRequestDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBookmarkService {

    BookmarkInfoDTO addBookmark(BookmarkRequestDTO dto, String userId);

    Boolean updateBookmark(BookmarkRequestDTO dto, String bookmarkId, String userId);

    Boolean delBookmark(String bookmarkId, String userId);

    List<BookmarkInfoDTO> listAllBookmark(String userId, String bookId);

    Page<BookmarkInfoDTO> listAllBookmark(String userId, String bookId, Integer pageNum, Integer pageSize);
}
