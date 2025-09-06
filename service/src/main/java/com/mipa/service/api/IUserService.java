package com.mipa.service.api;

import com.mipa.common.userDTO.UserInfoDTO;
import com.mipa.common.userDTO.UserRegisterDTO;

import java.util.Optional;

public interface IUserService {
    boolean save(UserRegisterDTO userRegisterDTO);
    Optional<UserInfoDTO> load(String userId);
}