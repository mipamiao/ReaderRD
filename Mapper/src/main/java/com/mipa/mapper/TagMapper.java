package com.mipa.mapper;

import com.mipa.model.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TagMapper {
    Tag selectById(@Param("id") String id);

    List<Tag> selectAll();

    int insert(Tag tag);

    int update(Tag tag);

    int delete(@Param("id") String id);

    Optional<Tag> selectByName(@Param("name") String name);

    List<Tag> selectByNames(@Param("names") List<String> names);
}
