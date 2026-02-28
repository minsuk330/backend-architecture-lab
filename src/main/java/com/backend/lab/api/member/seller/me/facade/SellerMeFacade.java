package com.backend.lab.api.member.seller.me.facade;

import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.api.member.global.facade.MemberMeFacade;
import com.backend.lab.domain.member.core.entity.dto.req.SellerUpdateReq;
import com.backend.lab.domain.member.core.service.SellerService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerMeFacade {

  private final SellerService sellerService;
  private final UploadFileService uploadFileService;
  private final MemberMeFacade memberMeFacade;

  public MemberGlobalResp me(Long memberId) {
    return memberMeFacade.me(memberId);
  }

  @Transactional
  public void update(Long userId, SellerUpdateReq req) {

    if(req.getProfileImageId() != null) {
      req.setProfileImage(uploadFileService.getById(req.getProfileImageId()));
    }

    sellerService.update(userId, req);
  }
}
