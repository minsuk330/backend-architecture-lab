package com.backend.lab.domain.member.core.service;

import com.backend.lab.api.admin.itemHistory.contactItem.dto.req.AdminContactCountSearchOptions;
import com.backend.lab.api.admin.itemHistory.contactItem.dto.resp.AdminContactCountResp;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAgentListOptions;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAgentSearchOptions;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.AgentLocalRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.AgentSocialRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.AgentUpdateReq;
import com.backend.lab.domain.member.core.entity.dto.req.SearchAgentOptions;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import com.backend.lab.domain.member.core.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgentService {

  private final MemberType memberType = MemberType.AGENT;

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  public Member getById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Agent"));
  }

  public Page<Member> search(SearchAgentOptions options) {
    return memberRepository.searchAgent(options);
  }

  public Page<Member> adminSearch(AdminAgentSearchOptions options) {
    return memberRepository.adminSearchAgent(options);
  }

  @Transactional
  public Member registerLocal(AgentLocalRegisterReq req) {

    String email = req.getEmail();
    if (memberRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
    }

    String phoneNumber = req.getPhoneNumber();
    if (memberRepository.existsByAgentAndPhoneNumber(MemberType.AGENT, phoneNumber)) {
      throw new BusinessException(ErrorCode.PHONE_NUMBER_DUPLICATED);
    }

    String encodedPassword = passwordEncoder.encode(req.getPassword());
    return memberRepository.save(
        Member.builder()
            .email(email)
            .password(encodedPassword)
            .profileImage(req.getProfileImage())
            .provider(ProviderType.LOCAL)

            .di(req.getDi())
            .isFirstLogin(false)
            .isBlocked(false)

            .type(memberType)

            .agentProperties(AgentProperties.builder()
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .businessName(req.getBusinessName())
                .address(req.getAddress())
                .addressDetail(req.getAddressDetail())
                .businessRegistration(req.getBusinessRegistration())
                .certification(req.getCertification())
                .registrationCertification(req.getRegistrationCertification())
                .build())

            .customerProperties(CustomerProperties.builder()
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .homePhoneNumber(null)
                .etcPhoneNumber(null)
                .funnel(null)
                .note(null)
                .build())

            .build()
    );

  }

  @Transactional
  public Member registerSocial(Long id, AgentSocialRegisterReq req) {


    String phoneNumber = req.getPhoneNumber();
    if (memberRepository.existsByAgentAndPhoneNumber(MemberType.AGENT, phoneNumber)) {
      throw new BusinessException(ErrorCode.PHONE_NUMBER_DUPLICATED);
    }

    Member member = this.getById(id);

    member.setType(memberType);

    member.setProfileImage(req.getProfileImage());
    member.setFirstLogin(false);
    member.setDi(req.getDi());

    AgentProperties agentProperties = member.getAgentProperties();
    if (agentProperties == null) {
      member.setAgentProperties(new AgentProperties());
      agentProperties = member.getAgentProperties();
    }
    agentProperties.setName(req.getName());
    agentProperties.setPhoneNumber(req.getPhoneNumber());
    agentProperties.setBusinessName(req.getBusinessName());
    agentProperties.setAddress(req.getAddress());
    agentProperties.setAddressDetail(req.getAddressDetail());

    agentProperties.setBusinessRegistration(req.getBusinessRegistration());
    agentProperties.setCertification(req.getCertification());
    agentProperties.setRegistrationCertification(req.getRegistrationCertification());

    CustomerProperties customerProperties = member.getCustomerProperties();
    if (customerProperties == null) {
      member.setCustomerProperties(new CustomerProperties());
      customerProperties = member.getCustomerProperties();
    }
    customerProperties.setName(req.getName());
    customerProperties.setPhoneNumber(req.getPhoneNumber());

    return memberRepository.save(member);
  }

  @Transactional
  public void update(Long id, AgentUpdateReq req) {
    Member member = this.getById(id);

    if (req.getEmail() != null) {
      if (!member.getEmail().equals(req.getEmail()) &&
              memberRepository.existsByEmail(req.getEmail())
      ) {
        throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
      }
      member.setEmail(req.getEmail());
    }
    if (req.getNewPassword() != null) {
      member.setPassword(passwordEncoder.encode(req.getNewPassword()));
    }
    if (req.getProfileImage() != null) {
      member.setProfileImage(req.getProfileImage());
    }

    AgentProperties agentProperties = member.getAgentProperties();
    if (req.getName() != null) {
      agentProperties.setName(req.getName());
    }
    if (req.getPhoneNumber() != null) {
      agentProperties.setPhoneNumber(req.getPhoneNumber());
    }
    if (req.getBusinessName() != null) {
      agentProperties.setBusinessName(req.getBusinessName());
    }
    if (req.getAddress() != null) {
      agentProperties.setAddress(req.getAddress());
    }
    if (req.getAddressDetail() != null) {
      agentProperties.setAddressDetail(req.getAddressDetail());
    }
    if (req.getBusinessRegistration() != null) {
      agentProperties.setBusinessRegistration(req.getBusinessRegistration());
    }
    if (req.getCertification() != null) {
      agentProperties.setCertification(req.getCertification());
    }
    if (req.getRegistrationCertification() != null) {
      agentProperties.setRegistrationCertification(req.getRegistrationCertification());
    }

    CustomerProperties customerProperties = member.getCustomerProperties();
    if (req.getName() != null) {
      customerProperties.setName(req.getName());
    }
    if (req.getPhoneNumber() != null) {
      customerProperties.setPhoneNumber(req.getPhoneNumber());
    }
  }

  public Page<AdminContactCountResp> searchWithCount(AdminContactCountSearchOptions options) {
    return memberRepository.searchAgentWithCount(options);
  }

  public List<Member> getAllActiveAgents() {
    return memberRepository.findAllActiveAgents(memberType);
  }

  public Page<Member> listActiveAgents(
      AdminAgentListOptions options) {
    return memberRepository.listActiveAgents(options);
  }
}
