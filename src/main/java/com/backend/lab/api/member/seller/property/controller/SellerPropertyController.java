package com.backend.lab.api.member.seller.property.controller;

import com.backend.lab.api.admin.PropertyRequest.dto.req.PropertyRequestSellerReq;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestSellerResp;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.api.member.seller.property.facade.SellerPropertyFacade;
import com.backend.lab.common.auth.annotation.RequireSellerRole;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireSellerRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/property")
@Tag(name = "[회원/매도자] 내 매물")
public class SellerPropertyController {

  private final SellerPropertyFacade sellerPropertyFacade;

  @Operation(summary = "내 매물 조회")
  @GetMapping("/list")
  public PageResp<PropertyListResp> list(
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return sellerPropertyFacade.getsWithPropertyMember(AuthUtil.getUserId(), options);
  }

  @Operation(summary = "매도자 매물 의뢰")
  @PostMapping("/request")
  public void createReq(
      @RequestBody PropertyRequestSellerReq req
  ) {
    sellerPropertyFacade.createReq(req, AuthUtil.getUserId());
  }

  @Operation(summary = "매물 의뢰 내역")
  @GetMapping("/request/list")
  public PageResp<PropertyRequestSellerResp> getsRequest(
      @ParameterObject @ModelAttribute PageOptions options
  ) {
    return sellerPropertyFacade.getsRequest(AuthUtil.getUserId(), options);
  }


}
