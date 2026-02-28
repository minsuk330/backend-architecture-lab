package com.backend.lab.domain.member.core.entity.dto.req;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MemberUpdateReq {

  private String email;
  private String newPassword;

  @Setter
  @JsonIgnore
  private UploadFile profileImage;
  private Long profileImageId;
}
