package com.mipa.mapper;

import com.mipa.common.Enum.OrderEnum;
import com.mipa.model.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ChapterMapper {
    Optional<Chapter> selectById(@Param("id") String id);

    Optional<Chapter> selectInfoById(@Param("id") String id);

    List<Chapter> selectAll();

    int insert(Chapter chapter);

    int update(Chapter chapter);

    int delete(@Param("id") String id);

    List<Chapter> selectInfoAllByBookId(@Param("bookId") String bookId, @Param("orderBy") OrderEnum orderBy, @Param("orderDirection") OrderEnum orderDirection);

    Optional<Chapter> selectInfoByBookIdAndOrder(@Param("bookId") String bookId, @Param("chapterOrder") Integer order);

    Optional<Chapter> selectByBookIdAndOrder(@Param("bookId") String bookId, @Param("chapterOrder") Integer chapterOrder);


}
