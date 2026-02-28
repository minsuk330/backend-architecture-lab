package com.backend.lab.common.exception;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@RequiredArgsConstructor
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException e) throws IOException, ServletException {

    response.setCharacterEncoding("UTF-8");
    response.setContentType(APPLICATION_JSON.toString());
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
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
