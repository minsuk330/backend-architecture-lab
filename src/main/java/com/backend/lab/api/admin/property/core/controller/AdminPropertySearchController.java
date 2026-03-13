package com.backend.lab.api.admin.property.core.controller;

import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatResp;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.application.port.in.property.search.GetPropertyByMapUseCase;
import com.backend.lab.application.port.in.property.search.GetPropertyListUseCase;
import com.backend.lab.application.port.in.property.search.GetPropertyStatUseCase;
import com.backend.lab.application.port.in.property.search.SearchPropertyUseCase;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/property")
@Tag(name = "[관리자] 매물 조회")
public class AdminPropertySearchController {

  private final SearchPropertyUseCase searchPropertyUseCase;
  private final GetPropertyListUseCase getPropertyListUseCase;
  private final GetPropertyByMapUseCase getPropertyByMapUseCase;
  private final GetPropertyStatUseCase getPropertyStatUseCase;

  @Operation(summary = "매물 목록 조회")
  @GetMapping("/list")
  public PageResp<PropertySearchResp> search(
      @RequestParam(required = false) List<Long> adminIds,
      @RequestParam(required = false) List<Long> bigCategoryIds,
      @RequestParam(required = false) List<Long> smallCategoryIds,
      @ModelAttribute SearchPropertyOptions options
  ) {
    return searchPropertyUseCase.search(options, adminIds, bigCategoryIds, smallCategoryIds);
  }


  @Operation(summary = "매물 리스트 조회")
  @GetMapping("home/list")
  public ListResp<PropertyListResp> getList() {
    return getPropertyListUseCase.getList();
  }

  @Operation(summary = "매물 지도 조회")
  @GetMapping("list/map")
  public ListResp<PropertyListResp> getByMap(
      @RequestParam(required = false) List<Long> adminIds,
      @RequestParam(required = false) List<Long> bigCategoryIds,
      @RequestParam(required = false) List<Long> smallCategoryIds,
      @ModelAttribute PropertyMapListReq req
  ) {
    return getPropertyByMapUseCase.getByMap(req, adminIds, bigCategoryIds, smallCategoryIds);
  }


  @Operation(summary = "매물 통계")
  @GetMapping("home/stat")
  public PropertyStatResp getStatistics(
      @RequestParam(required = false) Long totalDepartmentId,
      @RequestParam(required = false) Long totalMajorCategoryId,
      @RequestParam(required = false) Long totalAdminId,
      @RequestParam(required = false) Long monthlyDepartmentId,
      @RequestParam(required = false) Long monthlyMajorCategoryId,
      @RequestParam(required = false) Long monthlyAdminId
  ) {
    return getPropertyStatUseCase.getStat(totalDepartmentId, totalMajorCategoryId, totalAdminId,
        monthlyAdminId, monthlyDepartmentId, monthlyMajorCategoryId);
  }

}
