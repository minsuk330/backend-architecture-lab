package com.backend.lab.domain.member.core.entity.dto.resp;

import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPropsResp {

  private String name;
  private String phoneNumber;

  private String businessName; // 사업자명
  private UploadFileResp businessRegistration; // 사업자 등록증
  private UploadFileResp certification; // 공인중개사 자격증
  private UploadFileResp registrationCertification; // 개설 등록증

  private String address; // 주소
  private String addressDetail;
  private Long adminId;
  private String adminName;
}
