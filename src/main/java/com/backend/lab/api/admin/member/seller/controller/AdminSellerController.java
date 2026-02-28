package com.backend.lab.api.admin.member.seller.controller;

import com.backend.lab.api.admin.member.seller.facade.AdminSellerFacade;
import com.backend.lab.api.admin.member.seller.facade.dto.req.AdminSellerSearchOptions;
import com.backend.lab.api.admin.member.seller.facade.dto.resp.AdminSellerFullResp;
import com.backend.lab.api.admin.member.seller.facade.dto.resp.AdminSellerSearchResp;
import com.backend.lab.api.admin.property.core.facade.AdminPropertyFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/seller")
@Tag(name = "[관리자/회원관리] 매도자")
public class AdminSellerController {

  private final AdminSellerFacade adminSellerFacade;
  private final AdminPropertyFacade adminPropertyFacade;

  @Operation(summary = "매도자 검색")
  @GetMapping
  public PageResp<AdminSellerSearchResp> search(
      @ModelAttribute AdminSellerSearchOptions options
  ) {
    return adminSellerFacade.search(options);
  }

  @Operation(summary = "매도자 상세 조회")
  @GetMapping("/{id}")
  public AdminSellerFullResp getById(
      @PathVariable("id") Long id
  ) {
    return adminSellerFacade.getById(id);
  }

  @Operation(summary = "등록된 매매 물건")
  @GetMapping("/{id}/property")
  public PageResp<PropertySearchResp> getProperties(
      @ParameterObject @ModelAttribute PageOptions options,
      @PathVariable Long id
  ) {
    return adminPropertyFacade.getsWithPropertyMember(id, options);
  }
}
