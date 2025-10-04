package com.mipa.mapper;

import com.mipa.model.ReaderComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReaderCommentMapper {
    ReaderComment selectById(@Param("id") String id);

    List<ReaderComment> selectAll();

    int insert(ReaderComment comment);

    int update(ReaderComment comment);

    int delete(@Param("id") String id);
}
