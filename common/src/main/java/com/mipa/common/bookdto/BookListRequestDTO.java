package com.mipa.common.bookdto;

import lombok.*;

import java.util.List;

@Data
public class BookListRequestDTO {
    Integer pageNumber;
    Integer pageSize;
    String category;
}
