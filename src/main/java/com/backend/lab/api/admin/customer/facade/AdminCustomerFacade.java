package com.backend.lab.api.admin.customer.facade;

import com.backend.lab.api.admin.customer.facade.dto.AdminCustomerResp;
import com.backend.lab.api.admin.customer.facade.dto.CustomerDeleteResp;
import com.backend.lab.api.admin.property.core.mapper.PropertyMapper;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerDTO;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerSearchOptions;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.service.CustomerService;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.member.memberWorkLog.service.MemberChangeDetectService;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCustomerFacade {

  private final PropertyMemberService propertyMemberService;
  private final CustomerService customerService;
  private final PropertyMapper propertyMapper;
  private final PropertyService propertyService;
  private final AdminService adminService;
  private final MemberChangeDetectService memberChangeDetectService;
  private final MemberService memberService;

  public PageResp<AdminCustomerResp> search(AdminCustomerSearchOptions options) {
    Page<Member> page = customerService.search(options);
    List<AdminCustomerResp> data = page.getContent().stream()
        .map(this::customerResp)
        .toList();
    return new PageResp<>(page, data);
  }

  @Transactional
  public AdminCustomerResp create(AdminCustomerDTO req, Long adminId, String adminIp) {
    Member member = customerService.create(req);

    Admin admin = adminService.getById(adminId);

    memberChangeDetectService.memberCreateWorkLog(req,admin,member,adminIp);


    return this.customerResp(member);
  }

  @Transactional
  public AdminCustomerResp update(Long id, AdminCustomerDTO req, Long adminId, String clientIp) {

    Admin admin = adminService.getById(adminId);

    Member beforeMember = memberService.getById(id);

    memberChangeDetectService.memberUpdateWorkLog(req,admin,beforeMember,clientIp);

    Member member = customerService.update(id, req);

    return this.customerResp(member);
  }

  @Transactional
  public CustomerDeleteResp delete(Long id) {
    Member member = customerService.getById(id);
    List<PropertyMember> properties = propertyMemberService.getByMember(member.getId());
    if (!properties.isEmpty()) {
      return CustomerDeleteResp.builder()
          .success(false)
          .build();
    }

    customerService.delete(member);
    return CustomerDeleteResp.builder()
        .success(true)
        .build();
  }

  public AdminCustomerResp customerResp(Member member) {

    CustomerProperties properties = member.getCustomerProperties();

    Long adminId = properties.getAdminId();
    String adminName = null;
    if (adminId != null) {
      adminName = adminService.getById(adminId).getName();
    }

    List<PropertyMember> rows = propertyMemberService.getByMember(member.getId());
    List<PropertySearchResp> propertiesResps = rows.stream()
        .map(PropertyMember::getPropertyId)
        .map(pid -> propertyMapper.propertySearchResp(propertyService.getById(pid)))
        .toList();

    return AdminCustomerResp.builder()
        .id(member.getId())
        .createdAt(member.getCreatedAt())
        .updatedAt(member.getUpdatedAt())

        .name(properties.getName())
        .gender(properties.getGender())
        .etc(properties.getEtc())
        .birth(properties.getBirth())
        .email(member.getEmail())
        .address(properties.getAddress())
        .phoneNumber(properties.getPhoneNumber())
        .homePhoneNumber(properties.getHomePhoneNumber())
        .funnel(properties.getFunnel())
        .note(properties.getNote())

        .adminId(adminId)
        .adminName(adminName)

        .companyType(properties.getCompanyType())
        .properties(propertiesResps)

        .build();
  }
}
