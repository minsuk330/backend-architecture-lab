package com.backend.lab.domain.member.core.entity.dto.req;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import lombok.Getter;

@Getter
public class BuyerLocalRegisterReq extends MemberLocalRegisterReq {

  private String name;
  private CompanyType companyType; // 개인 / 법인
  private String phoneNumber;

  private String preferSidoCode; // 선호 시도 코드
  private String preferSigunguCode; // 선호 시군구 코드
  private String preferBjdongCode; // 선호 법정동 코드

  private Integer minPreferPrice;
  private Integer maxPreferPrice;
}
