package com.backend.lab.api.member.agent.auth.facade;

import com.backend.lab.api.member.common.auth.dto.resp.MemberWithTokenResp;
import com.backend.lab.api.member.global.facade.MemberGlobalFacade;
import com.backend.lab.common.auth.jwt.Jwt;
import com.backend.lab.common.auth.jwt.Jwt.Claims;
import com.backend.lab.common.auth.jwt.TokenResponse;
import com.backend.lab.common.auth.jwt.UserType;
import com.backend.lab.domain.agentItem.core.service.AgentItemService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.AgentLocalRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.AgentSocialRegisterReq;
import com.backend.lab.domain.member.core.service.AgentService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgentAuthFacade {

  private final Jwt jwt;
  private final AgentService agentService;
  private final AgentItemService agentItemService;
  private final UploadFileService uploadFileService;
  private final MemberGlobalFacade memberGlobalFacade;

  @Transactional
  public MemberWithTokenResp registerLocal(AgentLocalRegisterReq req) {

    if (req.getProfileImageId() != null) {
      req.setProfileImage(uploadFileService.getById(req.getProfileImageId()));
    }

    if (req.getCertificationId() != null) {
      req.setCertification(uploadFileService.getById(req.getCertificationId()));
    }

    if (req.getRegistrationCertificationId() != null) {
      req.setRegistrationCertification(
          uploadFileService.getById(req.getRegistrationCertificationId()));
    }

    if (req.getBusinessRegistrationId() != null) {
      req.setBusinessRegistration(uploadFileService.getById(req.getBusinessRegistrationId()));
    }

    Member member = agentService.registerLocal(req);
    agentItemService.createDefault(member);
    TokenResponse token = jwt.generateAllToken(
        Claims.from(member.getId(), member.getAuthorities(), UserType.MEMBER));
    return MemberWithTokenResp.builder()
        .member(memberGlobalFacade.memberGlobalResp(member))
        .token(token)
        .build();
  }

  @Transactional
  public MemberWithTokenResp registerSocial(Long userId, AgentSocialRegisterReq req) {

    if (req.getProfileImageId() != null) {
      req.setProfileImage(uploadFileService.getById(req.getProfileImageId()));
    }

    if (req.getCertificationId() != null) {
      req.setCertification(uploadFileService.getById(req.getCertificationId()));
    }

    if (req.getRegistrationCertificationId() != null) {
      req.setRegistrationCertification(
          uploadFileService.getById(req.getRegistrationCertificationId()));
    }

    if (req.getBusinessRegistrationId() != null) {
      req.setBusinessRegistration(uploadFileService.getById(req.getBusinessRegistrationId()));
    }

    Member member = agentService.registerSocial(userId, req);
    if (agentItemService.getOptByAgent(member).isEmpty()) {
      agentItemService.createDefault(member);
    }
    TokenResponse token = jwt.generateAllToken(
        Claims.from(member.getId(), member.getAuthorities(), UserType.MEMBER));
    return MemberWithTokenResp.builder()
        .member(memberGlobalFacade.memberGlobalResp(member))
        .token(token)
        .build();
  }
}
