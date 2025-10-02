package com.mipa.mapper;

import com.mipa.model.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TagMapper {
    Tag selectById(@Param("id") String id);

    List<Tag> selectAll();

    int insert(Tag tag);

    int update(Tag tag);

    int delete(@Param("id") String id);
}
