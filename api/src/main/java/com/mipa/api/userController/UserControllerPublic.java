package com.mipa.api.userController;

import com.mipa.auth.Security.JwtUtil;
import com.mipa.auth.Security.TokenInfo;
import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.response.ApiResponse;
import com.mipa.common.userDTO.UserLoginDTO;
import com.mipa.common.userDTO.UserLoginResponseDTO;
import com.mipa.common.userDTO.UserRegisterDTO;
import com.mipa.service.api.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/public/auth", produces = "application/json")
public class UserControllerPublic {

    @Autowired
    IUserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping(value = "/login", consumes = "application/json")
    public ApiResponse<UserLoginResponseDTO> logIn(
            @RequestBody UserLoginDTO requestDTO) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDTO.getUserName(), requestDTO.getPassword()));
            var userSec = (UserSecurity) auth.getPrincipal();
            TokenInfo tokenInfo = new TokenInfo(userSec.getUsername(),
                    auth.getAuthorities().iterator().next().getAuthority(),
                    userSec.getUserId());
            String token = JwtUtil.generateToken(tokenInfo);
            var userInfoDTO = userService.load(userSec.getUserId());
            if (userInfoDTO.isPresent()) {
                return ApiResponse.success(new UserLoginResponseDTO(token, userInfoDTO.get()));
            } else {
                return ApiResponse.unauthorized(null);
            }

        } catch (AuthenticationException e) {
            return ApiResponse.unauthorized(null);
        }
    }

    @PostMapping(value = "/register", consumes = "application/json" )
    public ApiResponse<Void> register(@RequestBody UserRegisterDTO userRegisterDTO) {

        var success = userService.save(userRegisterDTO);
        if (success) {
            return ApiResponse.success(null);
        } else {
            return ApiResponse.status(HttpStatus.CONFLICT, null);
        }
    }

}
