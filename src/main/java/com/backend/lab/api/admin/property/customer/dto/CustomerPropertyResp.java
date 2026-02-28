package com.backend.lab.api.admin.property.customer.dto;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPropertyResp {

  private String name;
  private String phoneNumber;
  private CompanyType companyType;
  private String etc;

}
