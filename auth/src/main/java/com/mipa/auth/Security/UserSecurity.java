package com.mipa.auth.Security;

import io.jsonwebtoken.Claims;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
//@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UserSecurity implements UserDetails {

    private String userName;
    private String userPassword;
    private String userId;

    private SimpleGrantedAuthority role ;

    public UserSecurity(String userName, String userPassword, String role, String userId) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.role = new SimpleGrantedAuthority(role);
        this.userId = userId;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    public static UserSecurity fromTokenClaim(Claims claims){
        String userName = claims.getSubject();
        String userRole = claims.get("role", String.class);
        String id = claims.get("userId", String.class);
        return new UserSecurity(userName, null, userRole, id);
    }

}
