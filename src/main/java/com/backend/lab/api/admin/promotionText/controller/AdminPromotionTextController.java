package com.backend.lab.api.admin.promotionText.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.promotionText.dto.PromotionTextResp;
import com.backend.lab.api.admin.promotionText.facade.AdminPromotionTextFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;

import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.promotionText.entity.dto.req.PromotionTextUpdateReq;
import com.backend.lab.domain.promotionText.entity.dto.resp.PromotionTextFieldResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/prom")
@Tag(name = "[관리자] 홍보문구 설정")
public class AdminPromotionTextController {

  private final AdminPromotionTextFacade adminPromotionTextFacade;


  @Operation(summary = "홍보 문구 조회")
  @GetMapping
  public ListResp<PromotionTextFieldResp> get(
  ) {
    return adminPromotionTextFacade.getPromotionText();
  }

  @PreAuthorize("hasAuthority('MANAGE_MARKETING_SLOGAN')")
  @Operation(summary = "홍보 문구 설정")
  @PostMapping
  public void create(
    @RequestBody PromotionTextUpdateReq req
  ) {
    adminPromotionTextFacade.create(req);
  }

  @Operation(summary = "매물 홍보문구 조회")
  @GetMapping("{propertyId}")
  public ListResp<PromotionTextResp> getPromotionText(
      @PathVariable("propertyId") Long propertyId
  ) {
    return adminPromotionTextFacade.getPromotionText(propertyId,getUserId());
  }

}
