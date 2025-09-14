package com.mipa.service;

import com.mipa.common.userDTO.UserInfoDTO;
import com.mipa.common.userDTO.UserRegisterDTO;
import com.mipa.convert.UserEntityConvert;
import com.mipa.repository.UserRepository;
import com.mipa.service.api.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepo;


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
}
