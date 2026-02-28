package com.backend.lab.api.member.agent.me.facade;

import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.api.member.global.facade.MemberMeFacade;
import com.backend.lab.domain.member.core.entity.dto.req.AgentUpdateReq;
import com.backend.lab.domain.member.core.service.AgentService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgentMeFacade {

  private final AgentService agentService;
  private final UploadFileService uploadFileService;
  private final MemberMeFacade memberMeFacade;

  @Transactional(readOnly = true)
  public MemberGlobalResp me(Long userId) {
    return memberMeFacade.me(userId);
  }

  @Transactional
  public void update(Long userId, AgentUpdateReq req) {

    if (req.getProfileImageId() != null) {
      req.setProfileImage(uploadFileService.getById(req.getProfileImageId()));
    }

    if (req.getBusinessRegistrationId() != null) {
      req.setBusinessRegistration(uploadFileService.getById(req.getBusinessRegistrationId()));
    }

    if (req.getCertificationId() != null) {
      req.setCertification(uploadFileService.getById(req.getCertificationId()));
    }

    if (req.getRegistrationCertificationId() != null) {
      req.setRegistrationCertification(uploadFileService.getById(req.getRegistrationCertificationId()));
    }

    agentService.update(userId, req);
  }
}
