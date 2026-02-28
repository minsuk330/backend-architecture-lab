package com.backend.lab.api.member.global.facade;

import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.member.audit.accessLog.entity.MemberAccessLog;
import com.backend.lab.domain.member.audit.accessLog.service.MemberAccessLogService;
import com.backend.lab.domain.member.core.entity.DeletedMember;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.resp.AgentPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.BuyerPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.SellerPropsResp;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.DeletedMemberService;
import com.backend.lab.domain.member.core.service.MemberService;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberGlobalFacade {

  private final AdminService adminService;
  private final MemberService memberService;
  private final DeletedMemberService deletedMemberService;
  private final MemberAccessLogService memberAccessLogService;
  private final UploadFileService uploadFileService;

  @Transactional(readOnly = true)
  public MemberGlobalResp memberGlobalResp(Member member) {

    MemberAccessLog accessLog = memberAccessLogService.getLatestByMemberId(member.getId());
    MemberGlobalResp resp = MemberGlobalResp.builder()
        .member(memberService.memberResp(member,
            uploadFileService.uploadFileResp(member.getProfileImage())))
        .lastLog(memberAccessLogService.memberAccessLogResp(accessLog))
        .build();

    MemberType type = member.getType();
    Admin admin;

    if (type == null) {
      return resp;
    }

    switch (type) {
      case BUYER -> {
        admin = adminService.getByIdNullable(member.getBuyerProperties().getAdminId());
        resp.setBuyer(memberService.buyerPropsResp(member, admin));
        break;
      }
      case SELLER -> {
        admin = adminService.getByIdNullable(member.getSellerProperties().getAdminId());
        resp.setSeller(memberService.sellerPropsResp(member, admin));
        break;
      }
      case AGENT -> {
        AgentProperties properties = member.getAgentProperties();
        admin = adminService.getByIdNullable(properties.getAdminId());
        UploadFileResp businessCertification = uploadFileService.uploadFileResp(
            properties.getBusinessRegistration());
        UploadFileResp certification = uploadFileService.uploadFileResp(
            properties.getCertification());
        UploadFileResp registrationCertification = uploadFileService.uploadFileResp(
            properties.getRegistrationCertification());

        resp.setAgent(memberService.agentPropsResp(member, businessCertification, certification,
            registrationCertification, admin));
        break;
      }
      case CUSTOMER -> {
        admin = adminService.getByIdNullable(member.getCustomerProperties().getAdminId());
        resp.setCustomer(memberService.customerPropsResp(member, admin));
        break;
      }
    }

    return resp;
  }

  @Transactional(readOnly = true)
  public MemberGlobalResp memberGlobalResp(DeletedMember member) {

    MemberAccessLog accessLog = memberAccessLogService.getLatestByMemberId(member.getId());
    MemberGlobalResp resp = MemberGlobalResp.builder()
        .member(deletedMemberService.memberResp(member,
            uploadFileService.uploadFileResp(member.getProfileImage())))
        .lastLog(memberAccessLogService.memberAccessLogResp(accessLog))
        .build();

    MemberType type = member.getType();
    Admin admin;
    switch (type) {
      case BUYER -> {
        admin = adminService.getByIdNullable(member.getBuyerProperties().getAdminId());
        resp.setBuyer(deletedMemberService.buyerPropsResp(member, admin));
        break;
      }
      case SELLER -> {
        admin = adminService.getByIdNullable(member.getSellerProperties().getAdminId());
        resp.setSeller(deletedMemberService.sellerPropsResp(member, admin));
        break;
      }
      case AGENT -> {
        AgentProperties properties = member.getAgentProperties();
        admin = adminService.getByIdNullable(properties.getAdminId());
        UploadFileResp businessCertification = uploadFileService.uploadFileResp(
            properties.getBusinessRegistration());
        UploadFileResp certification = uploadFileService.uploadFileResp(
            properties.getCertification());
        UploadFileResp registrationCertification = uploadFileService.uploadFileResp(
            properties.getRegistrationCertification());

        resp.setAgent(deletedMemberService.agentPropsResp(member, businessCertification, certification,
            registrationCertification, admin));
        break;
      }
      case CUSTOMER -> {
        admin = adminService.getByIdNullable(member.getCustomerProperties().getAdminId());
        resp.setCustomer(deletedMemberService.customerPropsResp(member, admin));
        break;
      }
    }

    return resp;
  }

  public AgentPropsResp agentPropsResp(Member member) {
    AgentProperties properties = member.getAgentProperties();
    Admin admin = adminService.getByIdNullable(properties.getAdminId());
    UploadFileResp businessCertification = uploadFileService.uploadFileResp(
        properties.getBusinessRegistration());
    UploadFileResp certification = uploadFileService.uploadFileResp(
        properties.getCertification());
    UploadFileResp registrationCertification = uploadFileService.uploadFileResp(
        properties.getRegistrationCertification());
    return memberService.agentPropsResp(
        member,
        businessCertification,
        certification,
        registrationCertification,
        admin
    );
  }

  public BuyerPropsResp buyerPropsResp(Member member) {
    Admin admin = adminService.getByIdNullable(member.getBuyerProperties().getAdminId());
    return memberService.buyerPropsResp(member, admin);
  }

  public SellerPropsResp sellerPropsResp(Member member) {
    Admin admin = adminService.getByIdNullable(member.getSellerProperties().getAdminId());
    return memberService.sellerPropsResp(member, admin);
  }
}
