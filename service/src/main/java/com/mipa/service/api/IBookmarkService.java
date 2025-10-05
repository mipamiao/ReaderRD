package com.mipa.service.api;

import com.mipa.common.vo.BookmarkInfoVO;
import com.mipa.common.dto.bookmarkdto.BookmarkRequestDTO;


import java.util.List;

public interface IBookmarkService {

    BookmarkInfoVO addBookmark(BookmarkRequestDTO dto, String userId);

    void updateBookmark(BookmarkRequestDTO dto, String bookmarkId, String userId);

    void delBookmark(String bookmarkId, String userId);

    List<BookmarkInfoVO> listAllBookmark(String userId, String bookId);

    //Page<BookmarkInfoDTO> listBookmark(String userId, String bookId, Integer pageNum, Integer pageSize);

    BookmarkInfoVO getBookmark(String userId, String bookId, String chapterId);
}
