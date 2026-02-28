package com.backend.lab.common.auth.session.repository;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.util.NetworkUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CustomSessionRepository {

  private static final Map<Long, String> sessionIdByAdminId = new ConcurrentHashMap<>();
  private static final Map<String, String> hostIpBySessionId = new ConcurrentHashMap<>();

  private static final Map<String, Object> store = new ConcurrentHashMap<>();
  private static final Map<String, LocalDateTime> expiryStore = new ConcurrentHashMap<>();

  public List<Long> getActiveAdminIds() {
    return sessionIdByAdminId.keySet().stream()
        .filter(id -> {
          String sid = sessionIdByAdminId.get(id);
          return !this.isExpired(sid);
        })
        .toList();
  }

  public void truncate() {
    for (Long adminId : sessionIdByAdminId.keySet()) {
      this.invalidate(adminId);
    }
  }

  public String getAdminSessionId(Long adminId, HttpServletRequest request) {

    String clientIp = NetworkUtil.getClientIp(request);
    String sessionId = sessionIdByAdminId.get(adminId);

    if (sessionId == null) {
      sessionId = this.generateSessionId();
      sessionIdByAdminId.put(adminId, sessionId);
      hostIpBySessionId.put(sessionId, clientIp);
      this.setSessionExpiryPlusHour(sessionId);
    } else {
      String originalClientIp = hostIpBySessionId.get(sessionId);
      if (originalClientIp == null || !originalClientIp.equals(clientIp)) {
        invalidate(adminId);
        sessionId = generateSessionId();
        sessionIdByAdminId.put(adminId, sessionId);
        hostIpBySessionId.put(sessionId, clientIp);
        this.setSessionExpiryPlusHour(sessionId);
      } else {
        this.setSessionExpiryPlusHour(sessionId);
      }
    }
    return sessionId;
  }

  public Object getAuthentication(String sessionId) {
    if (isExpired(sessionId)) {
      throw new BusinessException(ErrorCode.SESSION_EXPIRED);
    }
    return store.get(sessionId);
  }

  public void invalidate(Long adminId) {
    String sessionId = sessionIdByAdminId.get(adminId);

    if (sessionId != null) {
      hostIpBySessionId.remove(sessionId);
      store.remove(sessionId);
      expiryStore.remove(sessionId);
      sessionIdByAdminId.remove(adminId);
    }
  }

  public String generateSessionId() {
    Set<String> sessionIds = this.sessionIds();
    String newId;
    do {
      newId = UUID.randomUUID().toString();
    } while (sessionIds.contains(newId));
    return newId;
  }

  public void setSessionExpiryPlusHour(String sessionId) {
    final int EXPIRY_HOUR = 12;
    expiryStore.put(sessionId, LocalDateTime.now().plusHours(EXPIRY_HOUR));
  }

  public boolean isExpired(String sessionId) {
    LocalDateTime expiredAt = expiryStore.get(sessionId);
    if (expiredAt == null) {
      return true;
    }
    return LocalDateTime.now().isAfter(expiredAt);
  }

  public String put(String key, Object value) {
    store.put(key, value);
    return key;
  }

  public void remove(String sessionId) {
    store.remove(sessionId);
  }

  public Object get(String key) {
    if (store.containsKey(key)) {
      return store.get(key);
    }
    return null;
  }

  private Set<String> sessionIds() {
    return store.keySet();
  }
}
