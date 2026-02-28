package com.backend.lab.common.auth.session;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

@RequiredArgsConstructor
public class CustomExpiredSessionStrategy implements SessionInformationExpiredStrategy {

  private final ObjectMapper objectMapper;

  @Override
  public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
      throws IOException, ServletException {

    HttpServletRequest request = event.getRequest();
    HttpServletResponse response = event.getResponse();

    response.setCharacterEncoding("UTF-8");
    response.setContentType(APPLICATION_JSON.toString());
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    ErrorCode errorCode = ErrorCode.SESSION_EXPIRED;
    ErrorResponse errorResponse = ErrorResponse.builder()
        .path(request.getRequestURI())
        .code(errorCode.getCode())
        .message(errorCode.getMessage())
        .build();

    PrintWriter writer = response.getWriter();
    writer.write(objectMapper.writeValueAsString(errorResponse));
    writer.flush();
    writer.close();
  }
}
