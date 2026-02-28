package com.backend.lab.api.admin.property.customer.facade;

import com.backend.lab.api.admin.property.customer.dto.CustomerPropertyResp;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.CustomerService;
import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerPropertyFacade {

  private final CustomerService customerService;
  private final PropertyMemberService propertyMemberService;


  //todo 어드민일 경우와 agent일 경우 그 외로 나눠야 함
  public ListResp<CustomerPropertyResp> getCustomer(Long propertyId) {
    List<PropertyMember> propertyMembers = propertyMemberService.getByProperty(propertyId);

    if (!propertyMembers.isEmpty()) {
      List<Long> memberIds = propertyMembers.stream().map(PropertyMember::getMemberId
      ).toList();

      List<Member> memberList = memberIds.stream().map(customerService::getById).toList();

      List<CustomerPropertyResp> list = memberList.stream()
          .map(this::customerPropertyResp).toList();
      return new ListResp<>(list);
    }
    return new ListResp<>(null);
  }

  private CustomerPropertyResp customerPropertyResp(Member member) {
    if (member.getType()== MemberType.CUSTOMER){
      return CustomerPropertyResp.builder()
          .name(member.getCustomerProperties().getName())
          .etc(member.getCustomerProperties().getEtc())
          .phoneNumber(member.getCustomerProperties().getPhoneNumber())
          .companyType(member.getCustomerProperties().getCompanyType())
          .build();
    }
    else {
      return CustomerPropertyResp.builder()
          .companyType(member.getSellerProperties().getCompanyType())
          .etc(member.getCustomerProperties().getEtc())
          .phoneNumber(member.getSellerProperties().getPhoneNumber())
          .name(member.getSellerProperties().getName())
          .build();
    }

  }
}
