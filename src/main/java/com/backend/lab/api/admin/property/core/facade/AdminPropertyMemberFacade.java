package com.backend.lab.api.admin.property.core.facade;

import com.backend.lab.api.admin.property.core.dto.req.PropertyCustomerCreateReq;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerDTO;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.CustomerService;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.member.memberWorkLog.service.MemberChangeDetectService;
import com.backend.lab.domain.propertyMember.entity.PropertyMember;
import com.backend.lab.domain.propertyMember.service.PropertyMemberService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPropertyMemberFacade {

  private final PropertyMemberService propertyMemberService;
  private final MemberService memberService;
  private final CustomerService customerService;
  private final MemberChangeDetectService memberChangeDetectService;
  private final AdminService adminService;

  @Transactional
  public void createPropertyMember(List<PropertyCustomerCreateReq> members, Long propertyId, Long adminId,String clientIp) {
    Admin admin = adminService.getById(adminId);
    if (members!=null) {
      members.forEach(member-> {
        //이거 id값으로 바꿔주기
        if(member.getIsNew()) {
          Member customer = customerService.createCustomer(member,adminId);
          propertyMemberService.create(customer.getId(), propertyId);
          AdminCustomerDTO adminCustomerDTO = createAdminCustomerDTO(member, adminId);

          memberChangeDetectService.memberCreateWorkLog(adminCustomerDTO,admin,customer,clientIp);
        }
        else {
          //처음 매물 생성 시 기존에 존재하던 맴버 ->매도자 고객 둘 다 가능함
          //근데 이때 수정하는 정보는 customerProperties이다.
          Member originalCustomer = memberService.getById(member.getMemberId());

          propertyMemberService.create(member.getMemberId(), propertyId);

          AdminCustomerDTO adminCustomerDTO = createAdminCustomerDTO(member, adminId);

          //customer가 이미 업데이트 된게 들어와서 변경감지가 안일어남 그럼? 업데이트 전을 미리 조회하면 되겠다
          memberChangeDetectService.memberUpdateWorkLog(adminCustomerDTO,admin,originalCustomer,clientIp);
          customerService.updateSellerAndCustomer(member, member.getMemberId());
        }
      });
    }
  }

  //고객이 없으면 그냥 추가하면 되는데 고객이 있는데 그 정보를 수정하면 업데이트 해줘야 함
  @Transactional
  public void updatePropertyMember(List<PropertyCustomerCreateReq> members, Long propertyId, Long adminId, String clientIp) {

    List<PropertyMember> existingPropertyMembers = propertyMemberService.getByProperty(propertyId);
    Admin admin = adminService.getById(adminId);

    if (members != null && !members.isEmpty()) {
      Set<Long> requestMemberIds = members.stream()
          .filter(member -> !member.getIsNew() && member.getMemberId() != null)
          .map(PropertyCustomerCreateReq::getMemberId)
          .collect(Collectors.toSet());

      // 요청에 없는 기존 PropertyMember들 삭제
      List<PropertyMember> membersToDelete = existingPropertyMembers.stream()
          .filter(propertyMember -> !requestMemberIds.contains(propertyMember.getMemberId()))
          .collect(Collectors.toList());

      // PropertyMember 삭제
      propertyMemberService.deleteAll(membersToDelete);

      members.forEach(member -> {
        if (member.getIsNew() || member.getMemberId() == null) {
          // 새로운 멤버 생성
          createPropertyMember(Arrays.asList(member), propertyId, adminId, clientIp);
        } else {
          // 기존 멤버 처리(customer와 seller둘 다 여기서 처리해야함)
          Optional<Member> existingMember = memberService.findById(member.getMemberId());
          if (existingMember.isPresent()) {
            Member originalCustomer = existingMember.get();
            if(existingMember.get().getType()==MemberType.CUSTOMER) {
              propertyMemberService.createIfNotExists(member.getMemberId(), propertyId);
              AdminCustomerDTO adminCustomerDTO = createAdminCustomerDTO(member, adminId);
              memberChangeDetectService.memberUpdateWorkLog(adminCustomerDTO, admin, originalCustomer, clientIp);
              customerService.updateCustomer(member, adminId);
            }
            else {
              propertyMemberService.createIfNotExists(member.getMemberId(), propertyId);
              customerService.updateSellerAndCustomer(member, member.getMemberId());
            }
          }
        }
      });
    } else {
      // members가 null이거나 비어있으면 모든 기존 멤버 삭제
      propertyMemberService.deleteAll(existingPropertyMembers);
    }
  }
  private AdminCustomerDTO createAdminCustomerDTO(PropertyCustomerCreateReq req, Long adminId) {

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
