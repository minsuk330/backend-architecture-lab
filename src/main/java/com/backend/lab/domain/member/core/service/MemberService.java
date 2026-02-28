package com.backend.lab.domain.member.core.service;

import com.backend.lab.common.auth.AuthenticateUserService;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.resp.AgentPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.BuyerPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.CustomerPropsResp;
import com.backend.lab.domain.member.core.entity.dto.resp.MemberResp;
import com.backend.lab.domain.member.core.entity.dto.resp.SellerPropsResp;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.entity.embedded.BuyerProperties;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.embedded.SellerProperties;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import com.backend.lab.domain.member.core.repository.MemberRepository;
import com.backend.lab.domain.property.pnuTable.service.PnuTableService;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements AuthenticateUserService {

  private final PnuTableService pnuTableService;
  private final MemberRepository memberRepository;

  @Override
  public Member getById(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Member"));
  }

  public Optional<Member> findById(Long memberId) {
    return memberRepository.findById(memberId);
  }

  public List<Member> gets(Set<Long> memberIds) {
    return memberRepository.findAllById(memberIds);
  }

  public List<Member> getsAgent() {
    return memberRepository.findAllAgent(MemberType.AGENT);
  }

  public boolean existByTypeAndPhoneNumber(String phoneNumber, MemberType memberType) {
    return switch (memberType) {
      case AGENT -> memberRepository.existsByAgentAndPhoneNumber(memberType, phoneNumber);
      case SELLER -> memberRepository.existsBySellerAndPhoneNumber(memberType, phoneNumber);
      case BUYER -> memberRepository.existsByBuyerAndPhoneNumber(memberType, phoneNumber);
      default -> true;
    };
  }

  public Member getByEmail(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Member"));
  }

  public Optional<Member> getOptionalByProviderId(ProviderType providerType, String providerId) {
    return memberRepository.findByProviderAndProviderId(providerType, providerId);
  }

  public void existById(Long memberId) {
    if (!memberRepository.existsById(memberId)) {
      throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Member");
    }
  }

  @Transactional
  public Member createBySocialLogin(ProviderType providerType, String providerId, String email) {

    if (memberRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
    }
    
    return memberRepository.save(
        Member.builder()
            .isBlocked(false)
            .isFirstLogin(true)

            .provider(providerType)
            .providerId(providerId)
            .email(email)
            .build()
    );
  }

  @Transactional
  public void updatePassword(Member member, String newHashedPassword) {
    member.setPassword(newHashedPassword);
  }

  @Transactional
  public void delete(Long userId) {
    Member member = this.getById(userId);
    memberRepository.delete(member);
  }

  public MemberResp memberResp(Member member, UploadFileResp profileImage) {
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

  public CustomerPropsResp customerPropsResp(Member member, Admin admin) {
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

  public AgentPropsResp agentPropsResp(Member member, UploadFileResp businessRegistration,
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
        .addressDetail(properties.getAddressDetail())
        .adminId(adminId)
        .adminName(adminName)
        .build();
  }

  public SellerPropsResp sellerPropsResp(Member member, Admin admin) {

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

  public BuyerPropsResp buyerPropsResp(Member member, Admin admin) {
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

  public boolean existsByEmail(String email) {
    return memberRepository.existsByEmail(email);
  }
}
