package com.backend.lab.api.member.buyer.me.facade;

import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.api.member.global.facade.MemberMeFacade;
import com.backend.lab.domain.member.core.entity.dto.req.BuyerUpdateReq;
import com.backend.lab.domain.member.core.service.BuyerService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyerMeFacade {

  private final BuyerService buyerService;
  private final UploadFileService uploadFileService;
  private final MemberMeFacade memberMeFacade;

  @Transactional(readOnly = true)
  public MemberGlobalResp me(Long memberId) {
    return memberMeFacade.me(memberId);
  }

  @Transactional
  public void update(Long userId, BuyerUpdateReq req) {

    if(req.getProfileImageId() != null) {
      req.setProfileImage(uploadFileService.getById(req.getProfileImageId()));
    }

    buyerService.update(userId, req);
  }
}
