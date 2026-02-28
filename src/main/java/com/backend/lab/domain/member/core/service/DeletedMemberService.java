package com.backend.lab.domain.member.core.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.member.core.entity.DeletedMember;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerSearchOptions;
import com.backend.lab.domain.member.core.entity.dto.resp.AgentPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.BuyerPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.CustomerPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.MemberResp;
import com.backend.lab.domain.member.core.entity.dto.resp.SellerPropsResp;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.entity.embedded.BuyerProperties;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.embedded.SellerProperties;
import com.backend.lab.domain.member.core.repository.DeletedMemberRepository;
import com.backend.lab.domain.property.pnuTable.service.PnuTableService;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeletedMemberService {

  private final PnuTableService pnuTableService;
  private final DeletedMemberRepository deletedMemberRepository;

  public DeletedMember getById(Long id) {
    return deletedMemberRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "삭제된 고객"));
  }

  public Page<DeletedMember> search(AdminCustomerSearchOptions options) {
    return deletedMemberRepository.searchCustomer(options);
  }

  @Transactional
  public void restore(Long id) {
    DeletedMember deletedMember = this.getById(id);
    deletedMember.setDeletedAt(null);
  }

  @Transactional
  public void remove(Long id) {
    DeletedMember deletedMember = this.getById(id);
    deletedMemberRepository.delete(deletedMember);
  }

  public MemberResp memberResp(DeletedMember member, UploadFileResp profileImage) {
    return MemberResp.builder()
        .id(member.getId())
        .createdAt(member.getCreatedAt())

        .email(member.getEmail())
        .profileImage(profileImage)
        .role(member.getType())
        .provider(member.getProvider())

        .isBlocked(member.isBlocked())
        .isFirstLogin(member.isFirstLogin())

        .type(member.getType())
        .build();
  }

  public CustomerPropsResp customerPropsResp(DeletedMember member, Admin admin) {
    CustomerProperties properties = member.getCustomerProperties();

    Long adminId = null;
    String adminName = null;

    if (admin != null) {
      adminId = admin.getId();
      adminName = admin.getName();
    }

    return CustomerPropsResp.builder()
        .name(properties.getName())
        .phoneNumber(properties.getPhoneNumber())
        .homePhoneNumber(properties.getHomePhoneNumber())
        .etcPhoneNumber(properties.getEtcPhoneNumber())
        .funnel(properties.getFunnel())
        .note(properties.getNote())

        .adminId(adminId)
        .adminName(adminName)

        .address(properties.getAddress())
        .birth(properties.getBirth())
        .companyType(properties.getCompanyType())
        .build();
  }

  public AgentPropsResp agentPropsResp(DeletedMember member, UploadFileResp businessRegistration,
      UploadFileResp certification, UploadFileResp registrationCertification, Admin admin) {
    AgentProperties properties = member.getAgentProperties();

    Long adminId = null;
    String adminName = null;

    if (admin != null) {
      adminId = admin.getId();
      adminName = admin.getName();
    }

    return AgentPropsResp.builder()
        .name(properties.getName())
        .phoneNumber(properties.getPhoneNumber())
        .businessName(properties.getBusinessName())
        .businessRegistration(businessRegistration)
        .certification(certification)
        .registrationCertification(registrationCertification)

        .address(properties.getAddress())
        .adminId(adminId)
        .adminName(adminName)
        .build();
  }

  public SellerPropsResp sellerPropsResp(DeletedMember member, Admin admin) {

    SellerProperties properties = member.getSellerProperties();

    Long adminId = null;
    String adminName = null;

    if (admin != null) {
      adminId = admin.getId();
      adminName = admin.getName();
    }

    return SellerPropsResp.builder()
        .name(properties.getName())
        .companyType(properties.getCompanyType())
        .phoneNumber(properties.getPhoneNumber())

        .adminId(adminId)
        .adminName(adminName)
        .build();
  }

  public BuyerPropsResp buyerPropsResp(DeletedMember member, Admin admin) {
    BuyerProperties properties = member.getBuyerProperties();

    Long adminId = null;
    String adminName = null;

    if (admin != null) {
      adminId = admin.getId();
      adminName = admin.getName();
    }

    return BuyerPropsResp.builder()
        .name(properties.getName())
        .companyType(properties.getCompanyType())
        .phoneNumber(properties.getPhoneNumber())

        .preferSidoCode(properties.getPreferSidoCode())
        .preferSidoName(pnuTableService.getSidoName(properties.getPreferSidoCode()))
        .preferSigunguCode(properties.getPreferSigunguCode())
        .preferSigunguName(pnuTableService.getSigunguName(
            properties.getPreferSidoCode(),
            properties.getPreferSigunguCode()
        ))
        .preferBjdongCode(properties.getPreferBjdongCode())
        .preferBjdongName(pnuTableService.getBjdongName(
            properties.getPreferSidoCode(),
            properties.getPreferSidoCode(),
            properties.getPreferBjdongCode())
        )
        .minPreferPrice(properties.getMinPreferPrice())
        .maxPreferPrice(properties.getMaxPreferPrice())

        .adminId(adminId)
        .adminName(adminName)
        .build();
  }
}
