package com.mipa.mapper;

import com.mipa.common.Enum.OrderEnum;
import com.mipa.model.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {

    Book selectById(@Param("id") String id, @Param("orderBy")OrderEnum orderEnum, @Param("orderDirection ") String orderDirection);

    List<Book> selectAll();

    int insert(Book user);

    int update(Book user);

    int delete(@Param("id") String id);

    List<Book> selectByCategory(@Param("category") String category);

    List<Book> selectByAuthorId(@Param("authorId") String authorId);

    List<Book> selectByKeyword(@Param("keyword") String keyword);

    int updateCoverUrl(@Param("id")String id, @Param("coverUrl") String coverUrl);

    int updateChapterCount(@Param("id") String id, @Param("chapterCount") String chapterCount);
}
