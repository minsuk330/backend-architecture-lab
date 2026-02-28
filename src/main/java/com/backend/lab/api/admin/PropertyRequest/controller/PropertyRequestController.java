package com.backend.lab.api.admin.PropertyRequest.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.PropertyRequest.dto.req.SearchPropertyRequestOptions;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestAgentResp;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestCustomerResp;
import com.backend.lab.api.admin.PropertyRequest.dto.resp.PropertyRequestSellerResp;
import com.backend.lab.api.admin.PropertyRequest.facade.PropertyRequestFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/request")
@Tag(name = "[관리자] 매물 등록 요청 설정")
public class PropertyRequestController {

  private final PropertyRequestFacade propertyRequestFacade;

  @Operation(summary = "공인중개사 매물 등록 요청 리스트")
  @GetMapping("/agent")
  public PageResp<PropertyRequestAgentResp> searchAgent (
    @ModelAttribute SearchPropertyRequestOptions options
  ) {
    return propertyRequestFacade.agentSearch(options);
  }

  @Operation(summary = "매도자 매물 등록 요청 리스트")
  @GetMapping("/seller")
  public PageResp<PropertyRequestSellerResp> searchSeller (
      @ModelAttribute SearchPropertyRequestOptions options
  ) {
    return propertyRequestFacade.sellerSearch(options);
  }

  @Operation(summary = "비회원 매물 등록 요청 리스트")
  @GetMapping("/customer")
  public PageResp<PropertyRequestCustomerResp> searchCustomer (
      @ModelAttribute SearchPropertyRequestOptions options
  ) {
    return propertyRequestFacade.customerSearch(options);
  }

  @PreAuthorize("hasAuthority('MANAGER_REGISTER_STATE')")
  @Operation(summary = "승인")
  @PutMapping("/approve/{id}")
  public void approve (
      @PathVariable("id") Long id
  ) {
    propertyRequestFacade.approve(id,getUserId());
  }

  @PreAuthorize("hasAuthority('MANAGER_REGISTER_STATE')")
  @Operation(summary = "반려")
  @PutMapping("/reject/{id}")
  public void reject (
      @PathVariable("id") Long id
  ) {
    propertyRequestFacade.reject(id);
  }

}
