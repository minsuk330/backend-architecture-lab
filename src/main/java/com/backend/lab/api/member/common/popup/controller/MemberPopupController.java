package com.backend.lab.api.member.common.popup.controller;

import static com.backend.lab.common.util.AuthUtil.getUserIdWithAnonymous;

import com.backend.lab.api.member.common.popup.facade.MemberPopupFacade;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.popup.entity.dto.resp.PopupResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member/popup")
@RequiredArgsConstructor
@Tag(name = "[회원/공용] 팝업")
public class MemberPopupController {

  private final MemberPopupFacade memberPopupFacade;

  @Operation(summary = "팝업 조회")
  @GetMapping
  public ListResp<PopupResp> gets() {
    return memberPopupFacade.gets(getUserIdWithAnonymous());
  }
}
