package com.mipa.mapper;

import com.mipa.common.vo.BookShelfVO;
import com.mipa.model.ReaderBookShelf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReaderBookShelfMapper {

    ReaderBookShelf selectById(@Param("id") String id);

    List<ReaderBookShelf> selectAll();

    int insert(ReaderBookShelf readerBookmall);

    int update(ReaderBookShelf readerBookmall);

    int delete(@Param("id") String id);

    Optional<ReaderBookShelf> selectByUserIdAndBookId(@Param("userId") String userId, @Param("bookId") String bookId);

    Optional<BookShelfVO> selectDetailAndCoverByUserIdAndBookId(@Param("userId") String userId, @Param("bookId") String bookId);

    List<ReaderBookShelf> selectByUserId(@Param("userId") String userId);

    int deleteByUserIdAndBookId(@Param("userId") String userId, @Param("bookId") String bookId);

}
