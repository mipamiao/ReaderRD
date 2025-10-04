package com.mipa.common.dto.bookdto;

import com.mipa.common.vo.BookWithTagAndAuthorNameVO;
import lombok.Data;

import java.util.List;

@Data
public class BookListResponseDTO {
    List<BookWithTagAndAuthorNameVO> books;
    Long total;
    Integer pageNumber;
    Integer pageSize;
}
