package com.backend.lab.common.auth.session;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {

  private String sessionId;
  private LocalDateTime expiredAt;
}
