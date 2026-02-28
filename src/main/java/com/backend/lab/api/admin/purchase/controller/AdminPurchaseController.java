package com.backend.lab.api.admin.purchase.controller;

import com.backend.lab.api.admin.purchase.facade.AdminPurchaseFacade;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.purchase.core.entity.dto.PurchaseResp;
import com.backend.lab.domain.purchase.core.entity.dto.SearchPurchaseOptions;
import com.backend.lab.domain.purchase.item.entity.dto.ProductResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('MANAGE_PURCHASE_HISTORY')")
@RestController
@RequestMapping("/admin/purchase_history")
@RequiredArgsConstructor
@Tag(name = "[관리자] 결제 내역")
public class AdminPurchaseController {

  private final AdminPurchaseFacade adminPurchaseFacade;

  @Operation(summary = "결제 내역 검색")
  @GetMapping
  public PageResp<PurchaseResp> gets(
      @ModelAttribute SearchPurchaseOptions options
  ) {
    return adminPurchaseFacade.gets(options);
  }

  @Operation(summary = "[필터용] 결제 상품 리스트")
  @GetMapping("/item")
  public ListResp<ProductResp> getProducts() {
    return adminPurchaseFacade.getProducts();
  }
}
