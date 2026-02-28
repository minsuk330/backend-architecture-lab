package com.backend.lab.api.admin.customer.facade.dto;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.common.entity.vo.GenderType;
import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.member.core.entity.vo.CustomerFunnel;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCustomerResp extends BaseResp {

  private String name;
  private GenderType gender;
  private LocalDate birth;
  private String email;
  private String address; // 주소
  private String etc;
  private String phoneNumber;
  private String homePhoneNumber;
  private CustomerFunnel funnel;
  private String note;

  private Long adminId; //담당자
  private String adminName;
  private CompanyType companyType;
  private List<PropertySearchResp> properties;
}
