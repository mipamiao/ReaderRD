package com.mipa.common.librarydto;

import com.mipa.common.bookdto.BookDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibraryRequestDTO {

    private String id;

    private String userId;

    private String bookId;

    private String lastReadChapterId;

    private LocalDateTime lastReadAt;
}
