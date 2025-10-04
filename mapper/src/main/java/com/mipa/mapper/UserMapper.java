package com.mipa.mapper;

import com.mipa.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> selectById(@Param("id") String id);

    List<User> selectAll();

    int insert(User user);

    int update(User user);

    int delete(@Param("id") String id);

    Optional<User> selectByName(@Param("name") String name);

    int updateAvatar(@Param("id") String id, @Param("avatarUrl") String avatarUrl);
}
