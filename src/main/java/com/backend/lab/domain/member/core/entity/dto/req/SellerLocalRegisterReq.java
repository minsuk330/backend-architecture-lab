package com.backend.lab.domain.member.core.entity.dto.req;

import com.backend.lab.domain.member.core.entity.vo.CompanyType;
import lombok.Getter;

@Getter
public class SellerLocalRegisterReq extends MemberLocalRegisterReq {

  private String name;
  private CompanyType companyType;
  private String phoneNumber;

}
