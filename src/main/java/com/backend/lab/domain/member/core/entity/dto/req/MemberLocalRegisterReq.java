package com.backend.lab.domain.member.core.entity.dto.req;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MemberLocalRegisterReq {

  @NotNull
  private String email;
  @NotNull
  private String password;


  private String di;

  @Setter
  @JsonIgnore
  private UploadFile profileImage;
  private Long profileImageId;
}
