package com.backend.lab.domain.admin.core.entity.dto.req;

import com.backend.lab.common.entity.vo.GenderType;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
public class AdminUpdateReq {

  @Email
  @NotEmpty
  private String email;
  private String password;
  @NotNull
  private String name;
  private LocalDate birth;
  private String phoneNumber;
  private String officePhoneNumber;

  @JsonIgnore
  @Setter
  private UploadFile profileImage;
  private Long profileImageId;

  private GenderType gender;

  @NotNull
  private AdminRole role;
}
