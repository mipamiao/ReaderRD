package com.mipa.service;

import com.mipa.auth.Security.UserSecurity;
import com.mipa.mapper.UserMapper;
import com.mipa.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userMapper.selectByName(username)
                .map(user -> {
                    log.debug("Loaded user: {}", user);
                    return new UserSecurity(
                            user.getName(),
                            user.getPassword(),
                            user.getRole(),
                            user.getId()
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }
}
