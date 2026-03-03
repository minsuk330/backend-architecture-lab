package com.backend.lab.api.admin.property.core.facade.strategy;

import com.backend.lab.api.admin.property.core.dto.req.PropertyCustomerCreateReq;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerDTO;
import com.backend.lab.domain.member.core.service.CustomerService;
import com.backend.lab.domain.member.memberWorkLog.service.MemberChangeDetectService;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewMemberStrategy implements PropertyMemberStrategy {

  private final CustomerService customerService;
  private final PropertyMemberService propertyMemberService;
  private final MemberChangeDetectService memberChangeDetectService;

  @Override
  public boolean supports(PropertyCustomerCreateReq req) {
    return Boolean.TRUE.equals(req.getIsNew());
  }

  @Override
  public void handle(PropertyCustomerCreateReq req, Long propertyId, Admin admin, String clientIp) {
    Member customer = customerService.createCustomer(req, admin.getId());
    propertyMemberService.create(customer.getId(), propertyId);

    AdminCustomerDTO dto = toAdminCustomerDTO(req, admin.getId());
    memberChangeDetectService.memberCreateWorkLog(dto, admin, customer, clientIp);
  }

  private AdminCustomerDTO toAdminCustomerDTO(PropertyCustomerCreateReq req, Long adminId) {
    return AdminCustomerDTO.builder()
        .name(req.getName())
        .phoneNumber(req.getPhoneNumber())
        .adminId(adminId)
        .companyType(req.getCompanyType())
        .etc(req.getEtc())
        .homePhoneNumber(req.getHomePhoneNumber())
        .funnel(req.getFunnel())
        .note(req.getNote())
        .build();
  }
}