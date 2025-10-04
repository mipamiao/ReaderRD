package com.mipa.mapper;

import com.mipa.model.ReaderBookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReaderBookmarkMapper {
    Optional<ReaderBookmark> selectById(@Param("id") String id);

    List<ReaderBookmark> selectAll();

    int insert(ReaderBookmark readerBookmark);

    int update(ReaderBookmark readerBookmark);

    int delete(@Param("id") String id);

    Optional<ReaderBookmark> selectByUserIdAndBookIdAndChapterId(
            @Param("userId") String userId, @Param("bookId") String bookId, @Param("chapterId") String chapterId);

    List<ReaderBookmark> selectByUserIdAndBookId(@Param("userId") String userId, @Param("bookId") String bookId);
}
