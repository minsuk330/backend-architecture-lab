package com.backend.lab.api.member.seller.me.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.api.member.seller.me.facade.SellerMeFacade;
import com.backend.lab.common.auth.annotation.RequireSellerRole;
import com.backend.lab.domain.member.core.entity.dto.req.SellerUpdateReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireSellerRole
@RestController
@RequestMapping("/seller/me")
@RequiredArgsConstructor
@Tag(name = "[회원/매도자] 내 정보 관리")
public class SellerMeController {

  private final SellerMeFacade sellerMeFacade;

  @Operation(summary = "내 정보 조회")
  @GetMapping
  public MemberGlobalResp me() {
    return sellerMeFacade.me(getUserId());
  }

  @Operation(summary = "내 정보 수정")
  @PutMapping
  public void update(
      @RequestBody SellerUpdateReq req
  ) {
    sellerMeFacade.update(getUserId(), req);
  }
}
