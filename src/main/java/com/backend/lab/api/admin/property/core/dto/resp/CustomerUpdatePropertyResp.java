package com.backend.lab.api.admin.property.core.dto.resp;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.member.core.entity.vo.CustomerFunnel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdatePropertyResp {

  private Long memberId;
  private boolean canUpdate;
  private String etc;
  private String name;
  private String phoneNumber;
  private CompanyType companyType;
  private String homePhoneNumber;
  private CustomerFunnel funnel;
  private String note;
}
