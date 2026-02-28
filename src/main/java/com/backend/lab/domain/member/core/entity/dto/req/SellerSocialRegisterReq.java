package com.backend.lab.domain.member.core.entity.dto.req;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SellerSocialRegisterReq {

  private String name;
  private CompanyType companyType;
  private String phoneNumber;

  private String di;

  @Setter
  @JsonIgnore
  private UploadFile profileImage;
  private Long profileImageId;

}
