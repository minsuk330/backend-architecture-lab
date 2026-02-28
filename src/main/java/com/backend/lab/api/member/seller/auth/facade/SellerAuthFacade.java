package com.backend.lab.api.member.seller.auth.facade;

import com.backend.lab.api.member.common.auth.dto.resp.MemberWithTokenResp;
import com.backend.lab.api.member.global.facade.MemberGlobalFacade;
import com.backend.lab.common.auth.jwt.Jwt;
import com.backend.lab.common.auth.jwt.Jwt.Claims;
import com.backend.lab.common.auth.jwt.TokenResponse;
import com.backend.lab.common.auth.jwt.UserType;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.SellerLocalRegisterReq;
import com.backend.lab.domain.member.core.entity.dto.req.SellerSocialRegisterReq;
import com.backend.lab.domain.member.core.service.SellerService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerAuthFacade {

  private final Jwt jwt;
  private final SellerService sellerService;
  private final UploadFileService uploadFileService;
  private final MemberGlobalFacade memberGlobalFacade;

  @Transactional
  public MemberWithTokenResp registerLocal(SellerLocalRegisterReq req) {

    if (req.getProfileImageId() != null) {
      req.setProfileImage(uploadFileService.getById(req.getProfileImageId()));
    }

    Member member = sellerService.registerLocal(req);
    TokenResponse token = jwt.generateAllToken(
        Claims.from(member.getId(), member.getAuthorities(), UserType.MEMBER));
    return MemberWithTokenResp.builder()
        .member(memberGlobalFacade.memberGlobalResp(member))
        .token(token)
        .build();
  }

  @Transactional
  public MemberWithTokenResp registerSocial(Long userId, SellerSocialRegisterReq req) {

    if (req.getProfileImageId() != null) {
      req.setProfileImage(uploadFileService.getById(req.getProfileImageId()));
    }

    Member member = sellerService.registerSocial(userId, req);
    TokenResponse token = jwt.generateAllToken(
        Claims.from(member.getId(), member.getAuthorities(), UserType.MEMBER));
    return MemberWithTokenResp.builder()
        .member(memberGlobalFacade.memberGlobalResp(member))
        .token(token)
        .build();

  }
}
