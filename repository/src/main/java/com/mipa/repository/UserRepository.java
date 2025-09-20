package com.mipa.repository;

import com.mipa.model.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, String> {
    Optional<UserEntity> findByUserName(String username);

    @Modifying
    @Query("Update UserEntity u set u.avatarUrl = :avatarUrl where u.userId = :userId")
    Integer updateAvatar(@Param("userId") String userId, @Param("avatarUrl") String avatarUrl);

}
