package com.mipa.api.userController;


import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.filedto.FileUploadResponseDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.common.userDTO.UserInfoDTO;
import com.mipa.service.FileService;
import com.mipa.service.api.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping(path = "/api/private/auth", produces = "application/json")
public class UserControllerPrivate {

    @Value("${data.settings.avatars.srcDir}")
    private String avatarsSrcDir ;

    @Autowired
    IUserService userService;

    @Autowired
    FileService fileService;

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

    @PostMapping(path = "/update-avatar", consumes = "multipart/form-data")
    public ApiResponse<String> uploadFile(
            @RequestParam("avatar") MultipartFile file,
            @AuthenticationPrincipal UserSecurity userSecurity) {
        var resultUrl = userService.updateAvatar(file, userSecurity.getUserId());
        if (resultUrl != null) {
            return ApiResponse.success(resultUrl);
        } else {
            return ApiResponse.status(HttpStatus.BAD_REQUEST, "Failed to upload avatar", null);
        }
    }
}
