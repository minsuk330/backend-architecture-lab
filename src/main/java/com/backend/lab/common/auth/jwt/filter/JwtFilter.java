package com.backend.lab.common.auth.jwt.filter;

import com.backend.lab.common.auth.AuthenticateUser;
import com.backend.lab.common.auth.AuthenticateUserService;
import com.backend.lab.common.auth.jwt.Jwt;
import com.backend.lab.common.auth.jwt.Jwt.Claims;
import com.backend.lab.common.auth.jwt.UserType;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.member.core.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final Jwt jwt;
  private final MemberService memberService;
  private final AdminService adminService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String token = getAccessToken(request);

      if (token != null) {
        try {
          Jwt.Claims claims = verify(token);

          UserType userType = claims.getUserType();

          AuthenticateUserService authenticateUserService = switch (userType) {
            case MEMBER -> memberService;
            case ADMIN -> adminService;
          };

          Long memberId = claims.getMemberId();
          List<? extends GrantedAuthority> authority = getAuthorities(claims);

          if (authority == null) {
            throw new IllegalArgumentException("권한이 없습니다.");
          }

          AuthenticateUser authenticateUser = authenticateUserService.getById(memberId);
          String[] authorityNames = authenticateUser.getAuthorities();
          List<? extends GrantedAuthority> authorities = this.getAuthorities(authorityNames);

          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
              new UsernamePasswordAuthenticationToken(memberId, null, authorities);
          SecurityContextHolder.getContext()
              .setAuthentication(usernamePasswordAuthenticationToken);
        } catch (Exception e) {
          log.warn("사용자 인증토큰 처리중 문제가 발생하였습니다 : {}", e.getMessage());
        }
      } else {
        log.debug("사용자 인증토큰이 존재하지 않습니다.");
      }
    } else {
      log.debug("이미 사용자 인증 객체가 존재합니다 : {}",
          SecurityContextHolder.getContext().getAuthentication());
    }
    filterChain.doFilter(request, response);
  }

  private String getAccessToken(HttpServletRequest request) {
    String accessToken = request.getHeader("Authorization");

    if (accessToken != null && !accessToken.isBlank()) {
      try {
        accessToken = accessToken.replace("Bearer ", "");
        return URLDecoder.decode(accessToken, StandardCharsets.UTF_8);
      } catch (Exception e) {
        log.warn(e.getMessage(), e);
      }
    }
    return null;
  }

  private Jwt.Claims verify(String token) {
    return jwt.verify(token);
  }

  private List<? extends GrantedAuthority> getAuthorities(Claims claims) {
    if (claims.getRoles() != null) {
      return Stream.of(claims.getRoles())
          .map(SimpleGrantedAuthority::new)
          .toList();
    } else {
      return List.of();
    }
  }

  private List<? extends GrantedAuthority> getAuthorities(String[] authorityNames) {
    return Stream.of(authorityNames)
        .map(SimpleGrantedAuthority::new)
        .toList();
  }
}
