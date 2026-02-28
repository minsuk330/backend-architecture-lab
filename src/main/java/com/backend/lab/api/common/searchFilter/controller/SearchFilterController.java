package com.backend.lab.api.common.searchFilter.controller;

import com.backend.lab.api.admin.property.core.dto.resp.PropertySellerSearchResp;
import com.backend.lab.api.admin.wishlist.dto.resp.WishlistGroupResp;
import com.backend.lab.api.common.searchFilter.dto.resp.AdminFilterResp;
import com.backend.lab.api.common.searchFilter.dto.resp.MajorCategoryFilterResp;
import com.backend.lab.api.common.searchFilter.dto.resp.MinerCategoryFilterResp;
import com.backend.lab.api.common.searchFilter.dto.resp.PropertySortResp;
import com.backend.lab.api.common.searchFilter.facade.SearchFilterFacade;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.member.core.entity.dto.req.SearchAgentOptions;
import com.backend.lab.domain.member.core.entity.dto.req.SearchSellerAndCustomerOptions;
import com.backend.lab.domain.member.core.entity.dto.resp.PropertyAgentSearchResp;
import com.backend.lab.domain.property.category.entity.vo.MenuType;
import com.backend.lab.domain.property.pnuTable.entity.dto.resp.PnuResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("common/filter")
@Tag(name = "[공통] 검색 필터 조회")
public class SearchFilterController {

  private final SearchFilterFacade searchFilterFacade;

  // ********************** 시도, 시군구, 법정동 조회 ****************************//
  @Operation(summary = "시도 조회")
  @GetMapping("/address/sido")
  public ListResp<PnuResp> getsSido() {
    return searchFilterFacade.getsSido();
  }

  @Operation(summary = "시군구 조회")
  @GetMapping("/address/sigungu")
  public ListResp<PnuResp> getsSigungu(
      @Parameter @RequestParam(name = "sidoCode", required = true) String sidoCode
  ) {
    return searchFilterFacade.getsSigungu(sidoCode);
  }

  @Operation(summary = "시도 조회")
  @GetMapping("/address/bjdong")
  public ListResp<PnuResp> getsBjd(
      @Parameter @RequestParam(name = "sidoCode", required = true) String sidoCode,
      @Parameter @RequestParam(name = "sigunguCode", required = true) String sigunguCode
  ) {
    return searchFilterFacade.getsBjd(sidoCode, sigunguCode);
  }
  // ******************************************************************************//

  @Operation(summary = "관리자 목록 조회")
  @GetMapping("/admin-names")
  public ListResp<AdminFilterResp> getAdminNames() {
    return searchFilterFacade.getAdminList();
  }

  @Operation(summary = "대분류 필터 조회")
  @GetMapping("/major-category")
  public ListResp<MajorCategoryFilterResp> getMajorCategory() {
    return searchFilterFacade.getMajorCategory();
  }

  @Operation(summary = "소분류 필터 조회")
  @GetMapping("/minor-category")
  public ListResp<MinerCategoryFilterResp> getMinerCategory() {
    return searchFilterFacade.getMinerCategory();
  }


  @Operation(summary = "매물 정렬 필터 조회")
  @GetMapping("/property-filter")
  public PropertySortResp getPropertySortOptions() {
    return searchFilterFacade.getPropertySortFilter();
  }

  @Operation(summary = "전속 매물 필터 조회")
  @GetMapping("/exclusive-search")
  public PageResp<PropertyAgentSearchResp> searchAgent(
      @ModelAttribute SearchAgentOptions options
  ) {
    return searchFilterFacade.searchAgents(options);
  }

  @Operation(summary = "등록된 회원 검색")
  @GetMapping("/search/seller-customer")
  public PageResp<PropertySellerSearchResp> searchSeller(
      @ModelAttribute SearchSellerAndCustomerOptions options
  ) {
    return searchFilterFacade.searchSellerAndCustomer(options);
  }


  @Operation(summary = "관심그룹 필터 조회")
  @GetMapping("/wishlist")
  public ListResp<WishlistGroupResp> getWishlist() {
    return searchFilterFacade.getWishlistGroupFilter();
  }

  @Operation(summary = "카테고리 메뉴타입 필터 조회")
  @GetMapping("/category-menu")
  public ListResp<MenuType> categoryMenu() {
    return searchFilterFacade.getCategoryMenu();
  }

  @Operation(summary = "홍보문구 리스트 조회")
  @GetMapping("/promotion")
  public ListResp<String> placeHolder() {
    return searchFilterFacade.getPlaceHolder();
  }

}

