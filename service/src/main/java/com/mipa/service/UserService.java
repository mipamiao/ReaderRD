package com.mipa.service;

import com.mipa.common.configuration.MyConfiguration;
import com.mipa.common.dto.userDTO.UserInfoDTO;
import com.mipa.common.dto.userDTO.UserRegisterDTO;
import com.mipa.mapper.UserMapper;
import com.mipa.model.User;
import com.mipa.service.api.IUserService;
import com.mipa.utils.IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    MyConfiguration config;

    @Autowired
    FileService fileService;


    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public boolean save(UserRegisterDTO userRegisterDTO) {
        var user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);
        user.setId(IdUtil.uuid());

        if (userMapper.selectByName(user.getName()).isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userMapper.insert(user);
            return true;
        }
        return false;
    }

    @Override
    public Optional<UserInfoDTO> load(String userId) {
        var user = userMapper.selectById(userId);
        return user.map(item -> {
            var dto = new UserInfoDTO();
            BeanUtils.copyProperties(item, dto);
            return dto;
        });
    }


    @Transactional
    private Boolean updateAvatar(String userId, String url) {
        var userOpt = userMapper.selectById(userId);
        if(userOpt.isPresent()){
            userMapper.updateAvatar(userId, url);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public String updateAvatar(MultipartFile file, String userId) {

        if(file.isEmpty())return null;
        var userOpt = userMapper.selectById(userId);
        if(userOpt.isEmpty())return null;

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

        return resultUrl;

    }
}
