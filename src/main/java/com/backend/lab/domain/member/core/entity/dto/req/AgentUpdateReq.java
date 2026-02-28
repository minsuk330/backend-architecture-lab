package com.backend.lab.domain.member.core.entity.dto.req;

import com.backend.lab.domain.uploadFile.entity.UploadFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
public class AgentUpdateReq extends MemberUpdateReq {

  private String name;
  private String phoneNumber;
  private String businessName; // 사업자명
  private String address;
  private String addressDetail;

  @JsonIgnore
  @Setter
  private UploadFile businessRegistration; // 사업자 등록증
  private Long businessRegistrationId; // 사업자 등록증 ID

  @JsonIgnore
  @Setter
  private UploadFile certification; // 공인중개사 자격증
  private Long certificationId; // 공인중개사 자격증 ID

  @JsonIgnore
  @Setter
  private UploadFile registrationCertification; // 개설 등록증
  private Long registrationCertificationId; // 개설 등록증 ID
}
