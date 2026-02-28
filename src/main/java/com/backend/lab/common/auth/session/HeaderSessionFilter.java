package com.backend.lab.common.auth.session;

import com.backend.lab.common.auth.session.repository.CustomSessionRepository;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class HeaderSessionFilter extends OncePerRequestFilter {

  private final CustomSessionRepository customSessionRepository;

  private final String[] allowedUri = {"/admin/auth/login/local", "/admin/auth/reset-password"};

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String sessionId = request.getHeader("X-KMORGAN-SID");

    try {

      for (String uri : allowedUri) {
        String requestURI = request.getRequestURI();
        if (requestURI.equals(uri)) {
          filterChain.doFilter(request, response);
          return;
        }
      }

      if (sessionId != null) {
        Authentication authentication = (Authentication) customSessionRepository.getAuthentication(
            sessionId);
        if (authentication != null) {
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    } catch (BusinessException e) {
      if (e.getErrorCode().equals(ErrorCode.SESSION_EXPIRED)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }
}
