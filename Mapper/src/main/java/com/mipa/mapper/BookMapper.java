package com.mipa.mapper;

import com.mipa.common.vo.BookWithAuthorVO;
import com.mipa.common.vo.BookWithTagAndAuthorNameVO;
import com.mipa.model.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BookMapper {

    Optional<Book> selectById(@Param("id") String id);

    List<Book> selectAll();

    int insert(Book book);

    int update(Book book);

    int delete(@Param("id") String id);

    List<Book> selectByCategory(@Param("category") String category);

    List<Book> selectByAuthorId(@Param("authorId") String authorId);

    List<Book> selectByKeyword(@Param("keyword") String keyword);

    Optional<BookWithAuthorVO> selectBookAndAuthorById(@Param("id") String id);

    List<BookWithAuthorVO> selectAllBookAndAuthor();

    List<BookWithAuthorVO> selectAllBookAndAuthorByCategory(@Param("category") String category);

    List<BookWithAuthorVO> selectAllBookAndAuthorByAuthorId(@Param("authorId") String authorId);

    int updateCoverUrl(@Param("id")String id, @Param("coverUrl") String coverUrl);

    int updateChapterCount(@Param("id") String id, @Param("chapterCount") Integer chapterCount);

    int updateUpdatedAtById(@Param("id") String id);
}
