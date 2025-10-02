package com.mipa.mapper;

import com.mipa.model.ReaderBookmall;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReaderBookmallMapper {
    ReaderBookmall selectById(@Param("id") String id);

    List<ReaderBookmall> selectAll();

    int insert(ReaderBookmall readerBookmall);

    int update(ReaderBookmall readerBookmall);

    int delete(@Param("id") String id);
}
