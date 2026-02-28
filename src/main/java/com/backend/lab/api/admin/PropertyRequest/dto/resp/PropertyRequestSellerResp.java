package com.backend.lab.api.admin.PropertyRequest.dto.resp;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import com.backend.lab.domain.propertyRequest.entity.dto.resp.PropertyRequestResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class PropertyRequestSellerResp extends PropertyRequestResp {

  private Long sellerId;
  private String sellerName;
  private String phoneNumber;
  private CompanyType companyType;

}
