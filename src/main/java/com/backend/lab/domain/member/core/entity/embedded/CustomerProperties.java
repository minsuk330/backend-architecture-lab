package com.backend.lab.domain.member.core.entity.embedded;

import com.backend.lab.common.entity.vo.GenderType;
import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.member.core.entity.vo.CustomerFunnel;
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
public class CustomerProperties {

  @Column(name = "customer_name")
  private String name;
  @Column(name = "customer_phone_number")
  private String phoneNumber;
  @Column(name = "customer_home_phone_number")
  private String homePhoneNumber;
  @Column(name = "customer_etc_phone_number")
  private String etcPhoneNumber;
  @Column(name = "customer_funnel")
  private CustomerFunnel funnel;
  @Column(name = "customer_note",length = 1000)
  private String note;
  @Column(name = "customer_manager_id")
  private Long adminId; //담당자
  @Column(name = "customer_gender")
  private GenderType gender;
  //이메일은 member에 있어서 뺌
  @Column(name ="customer_address")
  private String address; // 주소
  @Column(name = "customer_birth")
  private LocalDate birth; // 생년월일
  @Enumerated(EnumType.STRING)
  @Column(name = "customer_company_type")
  private CompanyType companyType;
  @Column(name = "customer_etc")
  private String etc;

}
