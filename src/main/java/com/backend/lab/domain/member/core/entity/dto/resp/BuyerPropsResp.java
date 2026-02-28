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
public class BuyerPropsResp {

  private String name;
  private CompanyType companyType; // 개인 / 법인
  private String phoneNumber;

  private String preferSidoCode; // 시도 코드
  private String preferSidoName; // 시도 이름
  private String preferSigunguCode; // 시군구 코드
  private String preferSigunguName; // 시군구 이름
  private String preferBjdongCode; // 법정동 코드
  private String preferBjdongName; // 법정동 이름

  private Integer minPreferPrice;
  private Integer maxPreferPrice;

  private Long adminId;
  private String adminName;

}
