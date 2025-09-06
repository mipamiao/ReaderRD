package com.mipa.common.userDTO;

import lombok.Data;

@Data
public class UserLoginResponseDTO {
    private String token;
    private UserInfoDTO userInfo;

    public UserLoginResponseDTO(String token, UserInfoDTO userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }

}
