package com.backend.lab.domain.member.core.entity.dto.resp;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerPropsResp {

  private String name;
  private CompanyType companyType; // 개인 / 법인
  private String phoneNumber;

  private Long adminId;
  private String adminName;

}
