package com.mipa.common.bookdto;

import lombok.Data;

import java.util.List;

@Data
public class BookListResponseDTO {
    List<BookDTO> books;
    Integer total;
    Integer pageNumber;
    Integer pageSize;
}
