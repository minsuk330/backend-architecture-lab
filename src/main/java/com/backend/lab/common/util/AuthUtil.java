package com.backend.lab.common.util;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

  public static Long getUserId() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null) {
        throw new IllegalAccessException("인증 정보가 없습니다.");
      }
      return (Long) authentication.getPrincipal();
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED);
    }
  }

  public static Long getUserIdWithAnonymous() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null) {
        return null;
      }

      if (authentication.getPrincipal().equals("anonymousUser")) {
        return null;
      }
      return (Long) authentication.getPrincipal();
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }
}
