package com.mipa.service;

import com.mipa.common.Constant.ExMsg;
import com.mipa.common.configuration.MyConfiguration;
import com.mipa.common.dto.userDTO.UserInfoDTO;
import com.mipa.common.dto.userDTO.UserRegisterDTO;
import com.mipa.common.exception.BizException;
import com.mipa.mapper.UserMapper;
import com.mipa.model.User;
import com.mipa.service.api.IUserService;
import com.mipa.utils.IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
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

    @Transactional
    @Override
    public void save(UserRegisterDTO userRegisterDTO) {
        var user = new User();
        BeanUtils.copyProperties(userRegisterDTO, user);
        user.setId(IdUtil.uuid());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try{
            userMapper.insert(user);
        }catch (DuplicateKeyException e){
            throw new  BizException(HttpStatus.BAD_REQUEST, ExMsg.USERNAME_EXIST);
        }catch (DataIntegrityViolationException e) {
            throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.DB_CONSTRAIN_FAILED);
        }
    }

    @Override
    public Optional<UserInfoDTO> load(String userId) {
        var user = userMapper.selectById(userId);
        if (user.isEmpty())
            throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.USER_NOT_EXIST);
        return user.map(item -> {
            var dto = new UserInfoDTO();
            BeanUtils.copyProperties(item, dto);
            return dto;
        });
    }


    @Transactional
    @Override
    public String updateAvatar(MultipartFile file, String userId) {

        if(file.isEmpty())return null;
        var userOpt = userMapper.selectById(userId);
        if(userOpt.isEmpty())
            throw new BizException(HttpStatus.BAD_REQUEST, ExMsg.USER_NOT_EXIST);

        fileService.createDirIfNotExist(config.avatarsDstDir);

        String newFilename = fileService.generateUniqueFileName(
                file.getOriginalFilename(), userId
        );

        Path path = Paths.get(fileService.combinePath(config.avatarsDstDir, newFilename));
        if (!fileService.saveSmall(file, path)) {
            return null;
        }
        var resultUrl = fileService.combinePath(config.avatarsSrcDir, newFilename);

        userMapper.updateAvatar(userId, resultUrl);

        if (userOpt.get().getAvatarUrl() != null) {
            var oldAvatarPath = userOpt.get().getAvatarUrl().replace(config.avatarsSrcDir, config.avatarsDstDir);
            fileService.deleteSmall(oldAvatarPath);
        }

        return resultUrl;

    }
}
