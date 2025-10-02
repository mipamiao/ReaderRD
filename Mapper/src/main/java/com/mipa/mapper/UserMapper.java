package com.mipa.mapper;

import com.mipa.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectById(@Param("id") String id);

    List<User> selectAll();

    int insert(User user);

    int update(User user);

    int delete(@Param("id") String id);
}
