package com.backend.lab.api.admin.property.core.dto.resp;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertySellerSearchResp {
  private String name;
  private String phoneNumber;
  private MemberType memberType;
  private CompanyType companyType;
  private Long memberId;

}
