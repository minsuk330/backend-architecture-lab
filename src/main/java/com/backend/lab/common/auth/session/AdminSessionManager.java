package com.backend.lab.common.auth.session;

import com.backend.lab.api.admin.auth.dto.resp.AdminAuthenticationToken;
import com.backend.lab.common.auth.session.repository.CustomSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSessionManager {

  private final CustomSessionRepository customSessionRepository;

  public List<Long> getActives() {
    return customSessionRepository.getActiveAdminIds();
  }

  public String register(HttpServletRequest req, AdminAuthenticationToken authenticationToken) {
    Long adminId = (Long) authenticationToken.getPrincipal();
    String sessionId = customSessionRepository.getAdminSessionId(adminId, req);
    customSessionRepository.put(sessionId, authenticationToken);
    return sessionId;
  }

  public String refresh(Long adminId, HttpServletRequest req) {
    String sessionId = customSessionRepository.getAdminSessionId(adminId, req);
    customSessionRepository.setSessionExpiryPlusHour(sessionId);
    return sessionId;
  }

  public void invalidate(HttpServletRequest req) {
    HttpSession session = req.getSession(false);
    if (session != null) {
      session.invalidate();
    }
    SecurityContextHolder.clearContext();
  }

  public void truncate() {
    customSessionRepository.truncate();
  }
}
