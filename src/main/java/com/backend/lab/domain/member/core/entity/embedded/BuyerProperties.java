package com.backend.lab.domain.member.core.entity.embedded;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.common.entity.vo.GenderType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerProperties {

  @Column(name = "buyer_name")
  private String name;
  @Enumerated(EnumType.STRING)
  @Column(name = "buyer_company_type")
  private CompanyType companyType; // 개인 / 법인
  @Column(name = "buyer_phone_number")
  private String phoneNumber;

  @Column(name ="buyer_address")
  private String address; // 주소
   @Column(name ="buyer_address_detail")
  private String addressDetail; // 주소

  @Enumerated(EnumType.STRING)
  @Column(name = "buyer_gender")
  private GenderType gender;

  @Column(name = "buyer_birth")
  private LocalDate birth; // 생년월일

  @Column(name = "buyer_manager_id")
  private Long adminId;

  private String preferSidoCode; // 시도 코드
  private String preferSigunguCode; // 시군구 코드
  private String preferBjdongCode; // 법정동 코드

  private Integer minPreferPrice;
  private Integer maxPreferPrice;
}

