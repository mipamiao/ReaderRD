package com.mipa.service;

import com.mipa.auth.Security.UserSecurity;
import com.mipa.model.UserEntity;
import com.mipa.repository.UserRepository;
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
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //todo 难看以后改写一下
        UserEntity user = userRepository.findByUserName(username).get();
        log.debug(user.toString());
        if(user == null) return null;
        UserDetails userSecurity = new UserSecurity(user.getUserName(), user.getPassword(), user.getRole(), user.getUserId());
        userSecurity.getAuthorities().forEach(a -> System.out.println(a.getAuthority()));
        return userSecurity;
    }
}
