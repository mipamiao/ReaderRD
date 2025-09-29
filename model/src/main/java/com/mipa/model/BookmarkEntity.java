package com.mipa.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookmarks")
public class BookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String bookId;

    @Column(nullable = false)
    private String chapterId;

    private String chapterTitle;

    private Integer page;

    private Integer charIndex;

    private String note;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    private LocalDateTime updateTime = LocalDateTime.now();
}
