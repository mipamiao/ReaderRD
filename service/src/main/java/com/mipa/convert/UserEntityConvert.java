package com.mipa.convert;


import com.mipa.common.userDTO.UserInfoDTO;
import com.mipa.common.userDTO.UserRegisterDTO;
import com.mipa.model.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserEntityConvert {

    public UserEntity fromUserRegisterDTO(UserRegisterDTO userRegisterDTO){
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userRegisterDTO.getUsername());
        userEntity.setPassword(userRegisterDTO.getPassword());
        userEntity.setEmail(userRegisterDTO.getEmail());
        userEntity.setRole(userRegisterDTO.getRole());
        userEntity.setCreatedAt(LocalDateTime.now());
        return userEntity;
    }

    public UserInfoDTO toUserInfoDTO(UserEntity userEntity) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(userEntity.getUserId());
        userInfoDTO.setUsername(userEntity.getUserName());
        userInfoDTO.setEmail(userEntity.getEmail());
        userInfoDTO.setRole(userEntity.getRole());
        userInfoDTO.setCreatedAt(userEntity.getCreatedAt());
        return userInfoDTO;
    }
}
