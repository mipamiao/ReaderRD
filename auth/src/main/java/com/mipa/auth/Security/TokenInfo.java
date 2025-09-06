package com.mipa.auth.Security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class TokenInfo {
    private String userName;
    private String userRole;
    private String userId;
}
