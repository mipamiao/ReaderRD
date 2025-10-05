package com.mipa.common.dto.bookmarkdto;

import com.mipa.common.vo.BookmarkInfoVO;

import java.util.List;

public class BookmarkInfoListDTO {
    List<BookmarkInfoVO> bookmarks;
    Integer total;
    Integer pageNumber;
    Integer pageSize;
}
