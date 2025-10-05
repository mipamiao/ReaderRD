package com.mipa.service.api;

import com.mipa.common.dto.userDTO.UserInfoDTO;
import com.mipa.common.dto.userDTO.UserRegisterDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface IUserService {
    void save(UserRegisterDTO userRegisterDTO);
    Optional<UserInfoDTO> load(String userId);

    String updateAvatar(MultipartFile file, String userId);
}