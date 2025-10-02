package com.mipa.mapper;

import com.mipa.model.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookMapper {

    Book selectById(@Param("id") String id);

    List<Book> selectAll();

    int insert(Book user);

    int update(Book user);

    int delete(@Param("id") String id);
}
