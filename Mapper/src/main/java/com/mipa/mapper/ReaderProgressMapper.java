package com.mipa.mapper;

import com.mipa.model.ReaderProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReaderProgressMapper {
    ReaderProgress selectById(@Param("id") String id);

    List<ReaderProgress> selectAll();

    int insert(ReaderProgress progress);

    int update(ReaderProgress progress);

    int delete(@Param("id") String id);
}
