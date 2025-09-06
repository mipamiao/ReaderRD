package com.mipa.api.userController;


import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.response.ApiResponse;
import com.mipa.common.userDTO.UserInfoDTO;
import com.mipa.service.api.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/private/auth", produces = "application/json")
public class UserControllerPrivate {

    @Autowired
    IUserService userService;

    @GetMapping(path = "/profile")
    public ApiResponse<UserInfoDTO> getUserProfile(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(required = true) String userId
            ){
        var userInfoDTO = userService.load(userId);
        if (userInfoDTO.isPresent()) {
            return ApiResponse.success(userInfoDTO.get());
        } else {
            return ApiResponse.status(HttpStatus.NOT_FOUND, "User not found", null);
        }
    }
}
