package com.backend.lab.api.member.buyer.me.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.member.buyer.me.facade.BuyerMeFacade;
import com.backend.lab.api.member.global.dto.resp.MemberGlobalResp;
import com.backend.lab.common.auth.annotation.RequireBuyerRole;
import com.backend.lab.domain.member.core.entity.dto.req.BuyerUpdateReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireBuyerRole
@RestController
@RequestMapping("/buyer/me")
@RequiredArgsConstructor
@Tag(name = "[회원/매수자] 내 정보 관리")
public class BuyerMeController {

  private final BuyerMeFacade buyerMeFacade;

  @Operation(summary = "내 정보 조회")
  @GetMapping
  public MemberGlobalResp me() {
    return buyerMeFacade.me(getUserId());
  }

  @Operation(summary = "내 정보 수정")
  @PutMapping
  public void update(
      @RequestBody BuyerUpdateReq req
  ) {
    buyerMeFacade.update(getUserId(), req);
  }

}
