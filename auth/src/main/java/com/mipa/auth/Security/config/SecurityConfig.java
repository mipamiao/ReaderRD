package com.mipa.auth.Security.config;
import com.mipa.auth.Security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //todo 使用https
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) //
                ).sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(registry -> registry  // 使用新的 Lambda 风格
                        .requestMatchers("/api/authen/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/api/public/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                ).formLogin(AbstractHttpConfigurer::disable).addFilterAt(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class).build();
    }
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter();
    }
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
