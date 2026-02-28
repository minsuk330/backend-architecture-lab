package com.backend.lab.api.member.common.property.promotionText.controller;

import static com.backend.lab.common.util.AuthUtil.getUserIdWithAnonymous;

import com.backend.lab.api.admin.promotionText.dto.PromotionTextResp;
import com.backend.lab.api.member.common.property.promotionText.facade.MemberPromotionFacade;
import com.backend.lab.common.entity.dto.resp.ListResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "[회원/공용] 홍보문구 조회")
@RequestMapping("/member/prom")
public class MemberPromotionTextController {

  private final MemberPromotionFacade memberPromotionFacade;

  @Operation(summary = "매물 홍보문구 조회")
  @GetMapping("{propertyId}")
  public ListResp<PromotionTextResp> getPromotionText(
      @PathVariable("propertyId") Long propertyId
  ) {
    return memberPromotionFacade.getPromotionText(propertyId,getUserIdWithAnonymous());
  }


}
