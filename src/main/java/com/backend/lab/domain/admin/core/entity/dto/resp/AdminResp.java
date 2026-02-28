package com.backend.lab.domain.admin.core.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.common.entity.vo.GenderType;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResp extends BaseResp {

  private String email;
  private String name;
  private LocalDate birth;
  private String phoneNumber;
  private String officePhoneNumber;
  private UploadFileResp profileImage;
  private GenderType gender;
  private AdminRole role;
}
