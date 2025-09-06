package com.mipa.common.userDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoDTO {
    private String userId;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
