package com.mipa.auth.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    log.info("Request URL: {}?{}", request.getRequestURI(), request.getQueryString());
    if (!request.getRequestURI().contains("private")) {
//                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
//                    new UserSecurity("any", "any","any","any"), null, List.of(new SimpleGrantedAuthority("ROLE_READER"))));
      filterChain.doFilter(request, response);
    } else {
      System.out.println("进入了Jwt过滤器");
      String token = extractToken(request);
      try {
        Claims claims = JwtUtil.parseToken(token);
          UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
              UserSecurity.fromTokenClaim(claims), null,
              List.of(new SimpleGrantedAuthority(claims.get("role", String.class))));
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.info("JWT 认证成功: 用户名={}, 角色={}", claims.getSubject(),
            claims.get("role", String.class));
      } catch (JwtException je) {
        log.warn("Jwt认证失败: {}", je.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Jwt认证失败");
        return;
      }
      filterChain.doFilter(request, response);
    }
  }

  private String extractToken(HttpServletRequest request) {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer")) {
      return token.substring(7);
    }
    return null;
  }
}
