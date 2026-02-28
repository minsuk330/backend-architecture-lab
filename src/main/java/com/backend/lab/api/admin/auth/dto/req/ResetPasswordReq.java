package com.backend.lab.api.admin.auth.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ResetPasswordReq {

  @NotNull
  private String email;
}
