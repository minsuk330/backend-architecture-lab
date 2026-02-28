package com.backend.lab.api.admin.category.controller;

import com.backend.lab.api.admin.category.facade.AdminCategoryFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.property.category.entity.dto.req.BigCategoryDTO;
import com.backend.lab.domain.property.category.entity.dto.req.CategoryRerankReq;
import com.backend.lab.domain.property.category.entity.dto.req.SmallCategoryDTO;
import com.backend.lab.domain.property.category.entity.dto.resp.BigCategoryResp;
import com.backend.lab.domain.property.category.entity.dto.resp.SmallCategoryResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
@Tag(name = "[관리자] 종류 관리")
public class AdminCategoryController {

  private final AdminCategoryFacade adminCategoryFacade;

  @Operation(summary = "대분류 조회")
  @GetMapping("/big")
  public ListResp<BigCategoryResp> getBigs() {
    return adminCategoryFacade.getBigs();
  }

  @PreAuthorize("hasAuthority('MANAGE_CATEGORY')")
  @Operation(summary = "대분류 생성")
  @PostMapping("/big")
  public void createBig(
      @RequestBody BigCategoryDTO dto
  ) {
    adminCategoryFacade.createBig(dto);
  }

  @PreAuthorize("hasAuthority('MANAGE_CATEGORY')")
  @Operation(summary = "대분류 우선순위 변경")
  @PostMapping("/big/rerank")
  public void rerankBig(
      @RequestBody CategoryRerankReq req
  ) {
    adminCategoryFacade.rerankBig(req);
  }

  @PreAuthorize("hasAuthority('MANAGE_CATEGORY')")
  @Operation(summary = "대분류 수정")
  @PutMapping("/big/{categoryId}")
  public void updateBig(
      @PathVariable("categoryId") Long id,
      @RequestBody BigCategoryDTO dto
  ) {
    adminCategoryFacade.updateBig(id, dto);
  }

  @PreAuthorize("hasAuthority('MANAGE_CATEGORY')")
  @Operation(summary = "대분류 삭제")
  @DeleteMapping("/big/{categoryId}")
  public void deleteBig(
      @PathVariable("categoryId") Long id
  ) {
    adminCategoryFacade.deleteBig(id);
  }

  @PreAuthorize("hasAuthority('MANAGE_CATEGORY')")
  @Operation(summary = "소분류 조회")
  @GetMapping("/small")
  public ListResp<SmallCategoryResp> getSmalls() {
    return adminCategoryFacade.getSmalls();
  }

  @PreAuthorize("hasAuthority('MANAGE_CATEGORY')")
  @Operation(summary = "소분류 생성")
  @PostMapping("/small")
  public void createSmall(
      @RequestBody SmallCategoryDTO dto
  ) {
    adminCategoryFacade.createSmall(dto);
  }

  @PreAuthorize("hasAuthority('MANAGE_CATEGORY')")
  @Operation(summary = "소분류 우선순위 변경")
  @PostMapping("/small/rerank")
  public void rerankSmall(
      @RequestBody CategoryRerankReq req
  ) {
    adminCategoryFacade.rerankSmall(req);
  }

  @PreAuthorize("hasAuthority('MANAGE_CATEGORY')")
  @Operation(summary = "소분류 수정")
  @PutMapping("/small/{categoryId}")
  public void createSmall(
      @PathVariable("categoryId") Long id,
      @RequestBody SmallCategoryDTO dto
  ) {
    adminCategoryFacade.updateSmall(id, dto);
  }

  @PreAuthorize("hasAuthority('MANAGE_CATEGORY')")
  @Operation(summary = "소분류 삭제")
  @DeleteMapping("/small/{categoryId}")
  public void deleteSmall(
      @PathVariable("categoryId") Long id
  ) {
    adminCategoryFacade.deleteSmall(id);
  }
}
