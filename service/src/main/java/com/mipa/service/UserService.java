package com.mipa.service;

import com.mipa.common.configuration.MyConfiguration;
import com.mipa.common.response.ApiResponse;
import com.mipa.common.userDTO.UserInfoDTO;
import com.mipa.common.userDTO.UserRegisterDTO;
import com.mipa.convert.UserEntityConvert;
import com.mipa.repository.BookRepository;
import com.mipa.repository.UserRepository;
import com.mipa.service.api.IUserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    MyConfiguration config;

    @Autowired
    FileService fileService;


    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public boolean save(UserRegisterDTO userRegisterDTO){
        var entity = UserEntityConvert.fromUserRegisterDTO(userRegisterDTO);

        if(userRepo.findByUserName(entity.getUserName()).isEmpty()){
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
            userRepo.save(entity);
            return true;
        }
        return false;
    }

    @Override
    public Optional<UserInfoDTO> load(String userId){
        var entity = userRepo.findById(userId);
        return entity.map(UserEntityConvert::toUserInfoDTO);
    }


    @Transactional
    private Boolean updateAvatar(String userId, String url) {
        var userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            return userRepo.updateAvatar(userId, url) == 1;
        }
        return false;
    }

    @Transactional
    @Override
    public String updateAvatar(MultipartFile file, String userId) {
        if (file.isEmpty()) return null;
        var userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) return null;

        fileService.createDirIfNotExist(config.avatarsDstDir);

        String newFilename = fileService.generateUniqueFileName(
                file.getOriginalFilename(), userId
        );

        Path path = Paths.get(fileService.combinePath(config.avatarsDstDir, newFilename));
        if (!fileService.saveSmall(file, path)) {
            return null;
        }
        var resultUrl = fileService.combinePath(config.avatarsSrcDir, newFilename);
        updateAvatar(userId, resultUrl);

        if (userOpt.get().getAvatarUrl() != null) {
            var oldAvatarPath = userOpt.get().getAvatarUrl().replace(config.avatarsSrcDir, config.avatarsDstDir);
            fileService.deleteSmall(oldAvatarPath);
        }

        return fileService.combinePath(config.dataNetHost, resultUrl);
    }
}
