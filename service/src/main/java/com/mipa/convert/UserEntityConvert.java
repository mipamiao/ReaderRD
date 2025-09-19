package com.mipa.convert;


import com.mipa.common.userDTO.UserInfoDTO;
import com.mipa.common.userDTO.UserRegisterDTO;
import com.mipa.model.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class UserEntityConvert {

    public static UserEntity fromUserRegisterDTO(UserRegisterDTO userRegisterDTO){
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userRegisterDTO.getUserName());
        userEntity.setPassword(userRegisterDTO.getPassword());
        userEntity.setEmail(userRegisterDTO.getEmail());
        userEntity.setRole(userRegisterDTO.getRole());
        userEntity.setCreatedAt(LocalDateTime.now());
        return userEntity;
    }

    public static UserInfoDTO toUserInfoDTO(UserEntity userEntity) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(userEntity.getUserId());
        userInfoDTO.setUserName(userEntity.getUserName());
        userInfoDTO.setEmail(userEntity.getEmail());
        userInfoDTO.setRole(userEntity.getRole());
        userInfoDTO.setCreatedAt(userEntity.getCreatedAt());
        userInfoDTO.setAvatarUrl(userEntity.getAvatarUrl());
        return userInfoDTO;
    }

    public static UserEntity specifyUserId(String userId){
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        return userEntity;
    }
}
