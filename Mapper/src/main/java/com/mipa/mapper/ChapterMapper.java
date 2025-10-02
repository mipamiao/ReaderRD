package com.mipa.mapper;

import com.mipa.model.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChapterMapper {
    Chapter selectById(@Param("id") String id);

    List<Chapter> selectAll();

    int insert(Chapter chapter);

    int update(Chapter chapter);

    int delete(@Param("id") String id);
}
