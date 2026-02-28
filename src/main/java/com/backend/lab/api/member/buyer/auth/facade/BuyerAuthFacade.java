package com.backend.lab.api.member.buyer.auth.facade;

import com.backend.lab.api.member.common.auth.dto.resp.MemberWithTokenResp;
import com.backend.lab.api.member.global.facade.MemberGlobalFacade;
import com.backend.lab.common.auth.jwt.Jwt;
import com.backend.lab.common.auth.jwt.Jwt.Claims;
import com.backend.lab.common.auth.jwt.TokenResponse;
import com.backend.lab.common.auth.jwt.UserType;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.BuyerLocalRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.BuyerSocialRegisterReq;
import com.backend.lab.domain.member.core.service.BuyerService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyerAuthFacade {

  private final Jwt jwt;
  private final BuyerService buyerService;
  private final UploadFileService uploadFileService;
  private final MemberGlobalFacade memberGlobalFacade;

  @Transactional
  public MemberWithTokenResp registerLocal(BuyerLocalRegisterReq req) {

    if(req.getProfileImageId() != null) {
      req.setProfileImage(uploadFileService.getById(req.getProfileImageId()));
    }

    Member member = buyerService.registerLocal(req);
    TokenResponse token = jwt.generateAllToken(
        Claims.from(member.getId(), member.getAuthorities(), UserType.MEMBER));
    return MemberWithTokenResp.builder()
        .member(memberGlobalFacade.memberGlobalResp(member))
        .token(token)
        .build();
  }

  @Transactional
  public MemberWithTokenResp registerSocial(Long userId, BuyerSocialRegisterReq req) {
    if (req.getProfileImageId() != null) {
      req.setProfileImage(uploadFileService.getById(req.getProfileImageId()));
    }

    Member member = buyerService.registerSocial(userId, req);
    TokenResponse token = jwt.generateAllToken(
        Claims.from(member.getId(), member.getAuthorities(), UserType.MEMBER));
    return MemberWithTokenResp.builder()
        .member(memberGlobalFacade.memberGlobalResp(member))
        .token(token)
        .build();
  }
}
