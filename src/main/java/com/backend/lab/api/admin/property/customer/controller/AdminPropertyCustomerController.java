package com.backend.lab.api.admin.property.customer.controller;

import com.backend.lab.api.admin.property.customer.dto.CustomerPropertyResp;
import com.backend.lab.api.admin.property.customer.facade.CustomerPropertyFacade;
import com.backend.lab.common.entity.dto.resp.ListResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/property")
@RequiredArgsConstructor
@Tag(name = "[관리자] 매물 매도자 조회")
public class AdminPropertyCustomerController {

  private final CustomerPropertyFacade customerPropertyFacade;

  @Operation(summary = "매도자 모달")
  @GetMapping("/customer/{propertyId}")
  public ListResp<CustomerPropertyResp> get(
      @PathVariable("propertyId") Long propertyId
  ) {
    return customerPropertyFacade.getCustomer(propertyId);
  }

}
