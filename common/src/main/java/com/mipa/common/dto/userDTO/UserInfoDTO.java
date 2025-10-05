package com.mipa.common.dto.userDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoDTO {
    private String id;
    private String name;
    private String email;
    private String role;
    private String description;
    private LocalDateTime createdAt;
    private String avatarUrl;
}
