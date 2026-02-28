package com.backend.lab.api.admin.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LocalLoginReq {

  @Schema(example = "admin@test.com")
  private String email;
  @Schema(example = "password")
  private String password;
}
