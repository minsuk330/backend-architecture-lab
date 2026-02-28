package com.backend.lab.api.admin.property.core.dto.req;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.member.core.entity.vo.CustomerFunnel;
import lombok.Getter;

@Getter
public class PropertyCustomerCreateReq {
  //맴버 구별 필드
  private Boolean isNew;

  private Long memberId;

  private String etc;

  private String name;
  private String phoneNumber;
  private CompanyType companyType;
  private String homePhoneNumber;
  private CustomerFunnel funnel;
  private String note;
}
