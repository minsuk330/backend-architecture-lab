package com.backend.lab.common.util;

import com.backend.lab.domain.admin.audit.accessLog.entity.vo.AgentType;
import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class NetworkUtil {
  private static final Pattern IPV4_PATTERN = Pattern.compile(
      "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
  );

  private static final Pattern IPV6_PATTERN = Pattern.compile(
      "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::1$|^::$"
  );

  public static String getClientIp(HttpServletRequest request) {
    String[] headers = {
        "X-Forwarded-For",
        "X-Real-IP",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED"
    };

    // 1단계: IPv4 주소 우선 검색
    for (String header : headers) {
      String ip = request.getHeader(header);
      if (isValidHeader(ip)) {
        // 콤마로 구분된 경우 첫 번째 IP 사용
        if (ip.contains(",")) {
          ip = ip.split(",")[0].trim();
        }
        if (isValidIPv4(ip) && !isLocalAddress(ip)) {
          return ip;
        }
      }
    }

    // remote address에서 IPv4 확인
    String remoteAddr = request.getRemoteAddr();
    if (isValidIPv4(remoteAddr) && !isLocalAddress(remoteAddr)) {
      return remoteAddr;
    }

    // 2단계: IPv6 주소 검색
    for (String header : headers) {
      String ip = request.getHeader(header);
      if (isValidHeader(ip)) {
        // 콤마로 구분된 경우 첫 번째 IP 사용
        if (ip.contains(",")) {
          ip = ip.split(",")[0].trim();
        }
        if (isValidIPv6(ip) && !isLocalAddress(ip)) {
          return ip;
        }
      }
    }

    // remote address에서 IPv6 확인
    if (isValidIPv6(remoteAddr) && !isLocalAddress(remoteAddr)) {
      return remoteAddr;
    }

    return "-";
  }

  private static boolean isValidHeader(String ip) {
    return ip != null &&
        !ip.isEmpty() &&
        !"unknown".equalsIgnoreCase(ip);
  }

  private static boolean isValidIPv4(String ip) {
    return ip != null && IPV4_PATTERN.matcher(ip).matches();
  }

  private static boolean isValidIPv6(String ip) {
    // 간단한 IPv6 체크 (더 정교한 검증 필요시 수정)
    return ip != null && (IPV6_PATTERN.matcher(ip).matches() ||
        ip.contains(":") && ip.length() > 2);
  }

  private static boolean isLocalAddress(String ip) {
    return "127.0.0.1".equals(ip) ||
        "0:0:0:0:0:0:0:1".equals(ip) ||
        "::1".equals(ip);
  }

  public static AgentType getAgentType(HttpServletRequest request) {
    String userAgent = request.getHeader("User-Agent");
    if (userAgent == null || userAgent.isEmpty()) {
      return AgentType.PC;
    }

    userAgent = userAgent.toLowerCase();

    // 태블릿 체크 (iPad 등)
    if (userAgent.contains("ipad") ||
        (userAgent.contains("android") && !userAgent.contains("mobile"))) {
      return AgentType.MOBILE;
    }

    // 모바일 체크
    if (userAgent.contains("android") && userAgent.contains("mobile") ||
        userAgent.contains("iphone") ||
        userAgent.contains("ipod") ||
        userAgent.contains("blackberry") ||
        userAgent.contains("iemobile") ||
        userAgent.contains("opera mini") ||
        userAgent.contains("mobile")) {
      return AgentType.MOBILE;
    }

    return AgentType.PC;
  }
}
