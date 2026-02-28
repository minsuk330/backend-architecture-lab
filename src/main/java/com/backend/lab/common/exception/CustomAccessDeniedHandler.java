package com.backend.lab.common.exception;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException e) throws IOException, ServletException {

    response.setCharacterEncoding("UTF-8");
    response.setContentType(APPLICATION_JSON.toString());
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    ErrorCode errorCode = ErrorCode.FORBIDDEN;
    ErrorResponse errorResponse = ErrorResponse.builder()
        .path(request.getRequestURI())
        .code(errorCode.getCode())
        .message(e.getMessage())
        .build();

    PrintWriter writer = response.getWriter();
    writer.write(objectMapper.writeValueAsString(errorResponse));
    writer.flush();
    writer.close();
  }
}
