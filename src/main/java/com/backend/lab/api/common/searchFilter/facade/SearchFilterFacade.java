package com.backend.lab.api.common.searchFilter.facade;

import com.backend.lab.api.admin.property.core.dto.resp.PropertySellerSearchResp;
import com.backend.lab.api.admin.wishlist.dto.resp.WishlistGroupResp;
import com.backend.lab.api.common.searchFilter.dto.resp.AdminFilterResp;
import com.backend.lab.api.common.searchFilter.dto.resp.MajorCategoryFilterResp;
import com.backend.lab.api.common.searchFilter.dto.resp.MinerCategoryFilterResp;
import com.backend.lab.api.common.searchFilter.dto.resp.PropertySortResp;

import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.dto.req.SearchAgentOptions;
import com.backend.lab.domain.member.core.entity.dto.req.SearchSellerAndCustomerOptions;
import com.backend.lab.domain.member.core.entity.dto.resp.PropertyAgentSearchResp;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.service.AgentService;
import com.backend.lab.domain.member.core.service.CustomerService;
import com.backend.lab.domain.promotionText.entity.vo.PromotionPlaceholder;
import com.backend.lab.domain.property.category.entity.Category;
import com.backend.lab.domain.property.category.entity.vo.CategoryType;
import com.backend.lab.domain.property.category.entity.vo.MenuType;
import com.backend.lab.domain.property.category.service.CategoryService;

import com.backend.lab.domain.property.core.entity.vo.PropertySortType;
import com.backend.lab.domain.wishlistGroup.entity.WishlistGroup;
import com.backend.lab.domain.wishlistGroup.service.WishlistGroupService;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.backend.lab.domain.property.pnuTable.entity.dto.resp.PnuResp;
import com.backend.lab.domain.property.pnuTable.service.PnuTableService;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchFilterFacade {

  public static final Map<String, Integer> SIDO_ORDER = new LinkedHashMap<>() {{
    put("11", 1);  // 서울특별시
    put("26", 2);  // 부산광역시
    put("28", 3);  // 인천광역시
    put("27", 4);  // 대구광역시
    put("30", 5);  // 대전광역시
    put("29", 6);  // 광주광역시
    put("31", 7);  // 울산광역시
    put("36", 8);  // 세종특별자치시
    put("41", 9);  // 경기도
    put("43", 10); // 충청북도
    put("44", 11); // 충청남도
    put("46", 12); // 전라남도
    put("47", 13); // 경상북도
    put("48", 14); // 경상남도
    put("51", 15); // 강원특별자치도
    put("52", 16); // 전북특별자치도
    put("50", 17); // 제주특별자치도
  }};

  private final AdminService adminService;
  private final CategoryService categoryService;
  private final AgentService agentService;
  private final CustomerService customerService;
  private final WishlistGroupService wishlistGroupService;
  private final PnuTableService pnuTableService; // 시도, 시군구, 법정동 조회


  public ListResp<AdminFilterResp> getAdminList() {
    List<Admin> gets = adminService.getsWithJobGrade();

    if (!gets.isEmpty()) {
      List<AdminFilterResp> list = gets.stream().map(admin ->
          AdminFilterResp.builder()
              .id(admin.getId())
              .name(admin.getName() +
                  (admin.getJobGrade() != null ? " " + admin.getJobGrade().getName() : ""))
              .build()).toList();
      return new ListResp<>(list);
    }
    return null;
  }
  //todo 공인중개사 매도자 정보 열람

  // ********************** 시도, 시군구, 법정동 조회 ****************************//
  public ListResp<PnuResp> getsSido() {
    List<PnuResp> data = pnuTableService.getsSido().stream()
        .sorted((p1, p2) -> {
          Integer order1 = SIDO_ORDER.getOrDefault(p1.getSido(), 999);
          Integer order2 = SIDO_ORDER.getOrDefault(p2.getSido(), 999);
          return order1.compareTo(order2);
        })
        .map(pnuTableService::pnuResp)
        .toList();
    return new ListResp<>(data);
  }

  public ListResp<PnuResp> getsSigungu(String sidoCode) {
    List<PnuResp> data = pnuTableService.getsSigungu(sidoCode).stream()
        .sorted((p1, p2) -> p1.getSigunguName().compareTo(p2.getSigunguName()))
        .map(pnuTableService::pnuResp)
        .toList();
    return new ListResp<>(data);
  }

  public ListResp<PnuResp> getsBjd(String sidoCode, String sigunguCode) {
    List<PnuResp> data = pnuTableService.getsBjd(sidoCode, sigunguCode).stream()
        .sorted((p1, p2) -> p1.getBjdName().compareTo(p2.getBjdName()))
        .map(pnuTableService::pnuResp)
        .toList();
    return new ListResp<>(data);
  }
  // ******************************************************************************//




  public ListResp<MajorCategoryFilterResp> getMajorCategory() {
    List<Category> categories = categoryService.getsByType(CategoryType.BIG);
    List<MajorCategoryFilterResp> list = categories.stream()
        .filter(category -> Boolean.TRUE.equals(category.getIsActive()))
        .sorted(Comparator.comparingInt(Category::getRank))
        .map(category ->
        MajorCategoryFilterResp.builder()
            .id(category.getId())
            .name(category.getName())
            .build()).toList();
    return new ListResp<>(list);
  }

  public ListResp<MinerCategoryFilterResp> getMinerCategory() {
    List<Category> categories = categoryService.getsByType(CategoryType.SMALL);
    List<MinerCategoryFilterResp> list = categories.stream()
        .sorted(Comparator.comparingInt(Category::getRank))
        .map(category ->
        MinerCategoryFilterResp.builder()
            .id(category.getId())
            .name(category.getName())
            .build()).toList();
    return new ListResp<>(list);
  }

  public PropertySortResp getPropertySortFilter() {
    return PropertySortResp.builder()
        .sortOptions(Arrays.asList(PropertySortType.values()))
        .defaultSort(PropertySortType.LATEST)
        .build();
  }

  public ListResp<MenuType> getCategoryMenu() {
    List<MenuType> list = Arrays.stream(MenuType.values()).toList();

    return new ListResp<>(list);
  }

  public ListResp<String> getPlaceHolder() {
      List<String> list = Arrays.stream(PromotionPlaceholder.values())
          .map(PromotionPlaceholder::getToken)
          .collect(Collectors.toList());

      return new ListResp<>(list);
  }

  public ListResp<WishlistGroupResp> getWishlistGroupFilter() {
    List<WishlistGroup> groups = wishlistGroupService.gets();

    List<WishlistGroupResp> list = groups.stream().map(wishlistGroupService::wishlistGroupResp)
        .toList();

    return new ListResp<>(list);
  }

  public PageResp<PropertySellerSearchResp> searchSellerAndCustomer(
      SearchSellerAndCustomerOptions options) {
    Page<Member> search = customerService.search(options);

    List<PropertySellerSearchResp> list = search.getContent().stream()
        .map(this::memberSellerSearchResp)
        .toList();

    return new PageResp<>(search, list);
  }
  //전속 검색

  public PageResp<PropertyAgentSearchResp> searchAgents(SearchAgentOptions options) {
    Page<Member> search = agentService.search(options);

    List<PropertyAgentSearchResp> list = search.getContent().stream()
        .map(this::memberAgentSearchResp)
        .toList();

    return new PageResp<>(search, list);
  }

  private PropertyAgentSearchResp memberAgentSearchResp(Member member) {
    if (member != null) {
      if (member.getAgentProperties() == null) {
        return null;
      }
      return PropertyAgentSearchResp.builder()
          .id(member.getId())

          .name(member.getAgentProperties().getName())
          .businessName(member.getAgentProperties().getBusinessName())
          .phoneNumber(member.getAgentProperties().getPhoneNumber())
          .address(member.getAgentProperties().getAddress())
          .build();
    }
    return null;

  }

  private PropertySellerSearchResp memberSellerSearchResp(Member member) {
    if (member == null || member.getType() == null) {
      return null;
    }

    if (member.getType() == MemberType.SELLER) {
      if (member.getSellerProperties() == null) {
        return null; //
      }
      return PropertySellerSearchResp.builder()
          .companyType(member.getSellerProperties().getCompanyType())
          .name(member.getSellerProperties().getName())
          .phoneNumber(member.getSellerProperties().getPhoneNumber())
          .memberType(member.getType())
          .memberId(member.getId())
          .build();
    }
    else if (member.getType() == MemberType.CUSTOMER) {
      if (member.getCustomerProperties() == null) {
        return null;
      }
      return PropertySellerSearchResp.builder()
          .name(member.getCustomerProperties().getName())
          .phoneNumber(member.getCustomerProperties().getPhoneNumber())
          .memberType(member.getType())
          .memberId(member.getId())
          .build();
    }
    return null;
  }


}
