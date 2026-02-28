package com.backend.lab.domain.property.core.repository;

import com.backend.lab.api.admin.property.core.dto.req.AreaFilter;
import com.backend.lab.api.admin.property.core.dto.req.PriceFilter;
import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.admin.wishlist.dto.req.SearchWishlistOptions;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.api.common.searchFilter.dto.vo.SortDirection;
import com.backend.lab.api.common.searchFilter.dto.vo.SortType;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.QMember;
import com.backend.lab.domain.property.category.entity.QCategory;
import com.backend.lab.domain.property.category.entity.vo.MenuType;
import com.backend.lab.domain.property.core.entity.QProperty;
import com.backend.lab.domain.property.core.entity.information.QFloorInformation;
import com.backend.lab.domain.property.core.entity.information.QLandInformation;
import com.backend.lab.domain.property.core.entity.information.QLedgeInformation;
import com.backend.lab.domain.property.core.entity.vo.PropertySortType;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.propertyMember.entity.QPropertyMember;
import com.backend.lab.domain.secret.entity.QSecret;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyRepositoryHelper {

  private final EntityManager entityManager;
  private final QProperty property = QProperty.property;
  private final QSecret secret = QSecret.secret;
  private final QPropertyMember propertyMember = QPropertyMember.propertyMember;
  private final QMember member = QMember.member;

  public BooleanBuilder buildSearchPredicates(SearchPropertyOptions options, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    BooleanBuilder builder = new BooleanBuilder();

    if (options.getQuery() != null && !options.getQuery().isBlank()) {
      String query = options.getQuery().trim();

      String[] keywords = query.split("\\s+");
      
      BooleanExpression addressQuery = null;

      for (String keyword : keywords) {
        BooleanExpression keywordMatch = 
            property.address.properties.jibunAddress.containsIgnoreCase(keyword)
            .or(property.address.properties.roadAddress.containsIgnoreCase(keyword));
        
        addressQuery = (addressQuery == null) ? keywordMatch : addressQuery.and(keywordMatch);
      }
      
      BooleanExpression finalQuery = property.buildingName.containsIgnoreCase(query)
          .or(property.id.stringValue().eq(query))
          .or(addressQuery);
          
      builder.and(finalQuery);
    }

    // 고객분류 - PropertyMember를 통해 연결된 Member의 type으로 필터링
    if (options.getMemberType() != null) {
      BooleanExpression memberTypeExists = JPAExpressions
          .selectOne()
          .from(propertyMember)
          .join(member).on(propertyMember.memberId.eq(member.id))
          .where(
              propertyMember.propertyId.eq(property.id)
                  .and(member.type.eq(options.getMemberType()))
                  .and(member.deletedAt.isNull())
          )
          .exists();

      builder.and(memberTypeExists);
    }

    //대분류
    if (bigCategoryIds != null && !bigCategoryIds.isEmpty()) {
      builder.and(property.bigCategory.id.in(bigCategoryIds));
    }

    // 소분류 (모든 카테고리를 포함해야 함)
    if (smallCategoryIds != null && !smallCategoryIds.isEmpty()) {
      for (Long categoryId : smallCategoryIds) {
        builder.and(property.smallCategories.any().id.eq(categoryId));
      }
    }

    // 담당자
    if (adminIds != null && !adminIds.isEmpty()) {
      builder.and(property.adminId.in(adminIds));
    }

    //시군구 - LIKE 패턴으로 최적화
    if (options.getSidoCode() != null) {
      if (options.getSigungnCode() != null) {
        if (options.getBjdCode() != null) {
          String pnuPrefix = options.getSidoCode() + options.getSigungnCode() + options.getBjdCode();
          builder.and(property.address.properties.pnu.like(pnuPrefix + "%"));
        } else {
          String pnuPrefix = options.getSidoCode() + options.getSigungnCode();
          builder.and(property.address.properties.pnu.like(pnuPrefix + "%"));
        }
      } else {
        String pnuPrefix = options.getSidoCode();
        builder.and(property.address.properties.pnu.like(pnuPrefix + "%"));
      }
    }

    // 진행 상태필터
    if (options.getStatus() != null) {
      switch (options.getStatus()) {
        case READY -> builder.and(property.status.eq(PropertyStatus.READY));
        case COMPLETE -> builder.and(property.status.eq(PropertyStatus.COMPLETE));
        case PENDING -> builder.and(property.status.eq(PropertyStatus.PENDING));
        case SOLD -> builder.and(property.status.eq(PropertyStatus.SOLD));
      }
    }

    //공개 비공개
    if (options.getIsPublic() != null) {
      builder.and(property.isPublic.eq(options.getIsPublic()));
    }

    //전속필터
    if (options.getExclusiveId() != null && options.getExclusiveId() > 0) {
      builder.and(property.exclusiveAgentId.eq(options.getExclusiveId()));
    }

    //지번필터
    jibunFilter(options.getMinJibun(), options.getMaxJibun(), builder);
    //가격 관련 필터
    addPriceRangeFilters(builder, options);

    //면적 관련 필터
    addAreaRangeFilters(builder, options);

    return builder;
  }

  private void jibunFilter(String minJibun, String maxJibun, BooleanBuilder builder) {
    String minJibunStr = minJibun;
    String maxJibunStr = maxJibun;

    Integer minBun = null;
    Integer maxBun = null;
    Integer minJi = null;
    Integer maxJi = null;

    if (StringUtils.isNotEmpty(minJibunStr)) {

      minJibunStr = minJibunStr.trim();

      if (minJibunStr.contains("-")) {
        String[] minSplit = minJibunStr.split("-");
        if (minSplit.length == 2) {

          if (StringUtils.isNumeric(minSplit[0])) {
            minBun = Integer.parseInt(minSplit[0]);
          }

          if (StringUtils.isNumeric(minSplit[1])) {
            minJi = Integer.parseInt(minSplit[1]);
          }
        }
      } else if (StringUtils.isNumeric(minJibunStr)) {
        minBun = Integer.parseInt(minJibunStr);
      }
    }

    if (StringUtils.isNotEmpty(maxJibunStr)) {

      maxJibunStr = maxJibunStr.trim();

      if (maxJibunStr.contains("-")) {
        String[] maxSplit = maxJibunStr.split("-");
        if (maxSplit.length == 2) {
          if (StringUtils.isNumeric(maxSplit[0])) {
            maxBun = Integer.parseInt(maxSplit[0]);
          }

          if (StringUtils.isNumeric(maxSplit[1])) {
            maxJi = Integer.parseInt(maxSplit[1]);
          }
        }
      } else if (StringUtils.isNumeric(maxJibunStr)) {
        maxBun = Integer.parseInt(maxJibunStr);
      }
    }

    //범위검색
    //둘 중 하나만 널이냐
    if (minBun != null || maxBun != null) {
      if (minBun != null && maxBun != null) {
        // 둘 다 있음 범위 검색
        builder.and(property.address.properties.bunCode.goe(minBun));
        builder.and(property.address.properties.bunCode.loe(maxBun));

        if (minJi != null) {
          builder.and(property.address.properties.jiCode.goe(minJi));
        }
        if (maxJi != null) {
          builder.and(property.address.properties.jiCode.loe(maxJi));
        }
      } else {
        // 둘 중 하나만 있음 정확히 일치하는 값 검색
        if (minBun != null) {
          builder.and(property.address.properties.bunCode.eq(minBun));
        }
        if (maxBun != null) {
          builder.and(property.address.properties.bunCode.eq(maxBun));
        }

        if (minJi != null) {
          builder.and(property.address.properties.jiCode.eq(minJi));
        }
        if (maxJi != null) {
          builder.and(property.address.properties.jiCode.eq(maxJi));
        }
      }
    }


  }

  public BooleanBuilder buildListPredicates(PropertyMapListReq options, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    BooleanBuilder builder = new BooleanBuilder();

    // 지도 좌표 범위 필터링 - null 체크 추가
    if (options.getLtLat() != null && options.getLtLng() != null &&
        options.getRbLat() != null && options.getRbLng() != null) {
      builder.and(property.address.isNotNull())
             .and(property.address.properties.isNotNull())
             .and(property.address.properties.lat.isNotNull())
             .and(property.address.properties.lng.isNotNull())
             .and(property.address.properties.lat.between(options.getRbLat(), options.getLtLat()))
             .and(property.address.properties.lng.between(options.getLtLng(), options.getRbLng()));
    }

    //대분류 - null 체크 추가
    if (bigCategoryIds != null && !bigCategoryIds.isEmpty()) {
      builder.and(property.bigCategory.isNotNull())
             .and(property.bigCategory.id.in(bigCategoryIds));
    }

    // 소분류 (모든 카테고리를 포함해야 함) - 최적화된 버전
    if (smallCategoryIds != null && !smallCategoryIds.isEmpty()) {
      // 서브쿼리로 해당 매물이 모든 소분류 카테고리를 가지고 있는지 확인
      QCategory smallCategory = new QCategory("smallCategory");
      BooleanExpression hasAllCategories = JPAExpressions
          .select(property.id)
          .from(property)
          .join(property.smallCategories, smallCategory)
          .where(property.id.eq(QProperty.property.id)
              .and(smallCategory.id.in(smallCategoryIds)))
          .groupBy(property.id)
          .having(smallCategory.id.countDistinct().eq((long) smallCategoryIds.size()))
          .exists();
      
      builder.and(hasAllCategories);
    }

    // 담당자
    if (adminIds != null && !adminIds.isEmpty()) {
      builder.and(property.adminId.in(adminIds));
    }

    jibunFilter(options.getMinJibun(), options.getMaxJibun(), builder);

    // 진행 상태필터
    if (options.getStatus() != null) {
      switch (options.getStatus()) {
        case READY -> builder.and(property.status.eq(PropertyStatus.READY));
        case COMPLETE -> builder.and(property.status.eq(PropertyStatus.COMPLETE));
        case PENDING -> builder.and(property.status.eq(PropertyStatus.PENDING));
        case SOLD -> builder.and(property.status.eq(PropertyStatus.SOLD));
      }
    }

    // 시도/시군구/법정동 필터 - LIKE 패턴으로 최적화
    if (options.getSidoCode() != null) {
      builder.and(property.address.isNotNull())
          .and(property.address.properties.isNotNull())
          .and(property.address.properties.pnu.isNotNull());

      if (options.getSigungnCode() != null) {
        if (options.getBjdCode() != null) {
          String pnuPrefix = options.getSidoCode() + options.getSigungnCode() + options.getBjdCode();
          builder.and(property.address.properties.pnu.like(pnuPrefix + "%"));
        } else {
          String pnuPrefix = options.getSidoCode() + options.getSigungnCode();
          builder.and(property.address.properties.pnu.like(pnuPrefix + "%"));
        }
      } else {
        String pnuPrefix = options.getSidoCode();
        builder.and(property.address.properties.pnu.like(pnuPrefix + "%"));
      }
    }

    //공개 비공개
    if (options.getIsPublic() != null) {
      builder.and(property.isPublic.eq(options.getIsPublic()));
    }


    //가격 관련 필터
    addPriceRangeFilters(builder, options);

    //면적 관련 필터
    addAreaRangeFilters(builder, options);

    return builder;
  }

  public BooleanBuilder buildWishlistSearchPredicates(SearchWishlistOptions options) {
    BooleanBuilder builder = new BooleanBuilder();

    if (options.getQuery() != null && !options.getQuery().isBlank()) {
      BooleanExpression query = property.buildingName.containsIgnoreCase(options.getQuery())
          .or(property.id.stringValue().containsIgnoreCase(options.getQuery()));
      builder.and(query);
    }

    if (options.getMinInterestCount() != null && options.getMaxInterestCount() != null
        && options.getMinInterestCount() <= options.getMaxInterestCount()) {
      builder.and(property.wishCount.between(
          options.getMinInterestCount(),
          options.getMaxInterestCount()
      ));
    }
    return builder;

  }

  public BooleanBuilder buildMenuTypesPredicate(List<MenuType> menuTypes) {
    BooleanBuilder predicates = new BooleanBuilder();


    if (menuTypes != null && !menuTypes.isEmpty()) {

      BooleanExpression menuTypeInBigCategory = property.bigCategory.menuTypes.any().in(menuTypes);

      BooleanExpression menuTypeInSmallCategory = property.smallCategories.any().menuTypes.any().in(menuTypes);
      
      predicates.and(menuTypeInBigCategory.or(menuTypeInSmallCategory));
    }

    return predicates;
  }

  public void addPriceRangeFilters(BooleanBuilder builder, PriceFilter filter) {

    // 매매가 범위 - null을 0으로 취급
    if (filter.getMinPrice() != null) {
      builder.and(property.price.properties.mmPrice.coalesce(0L).goe(filter.getMinPrice()));
    }
    if (filter.getMaxPrice() != null) {
      builder.and(property.price.properties.mmPrice.coalesce(0L).loe(filter.getMaxPrice()));
    }

    // 평단가 범위 - null을 0으로 취급
    if (filter.getMinPyengPrice() != null) {
      if (filter.getMinPyengPrice() <= 0) {
        // 최소값이 0 이하면 null도 포함
        builder.and(property.price.properties.pyeongPrice.isNull()
            .or(property.price.properties.pyeongPrice.goe(filter.getMinPyengPrice())));
      } else {
        // 최소값이 0 초과면 null 제외
        builder.and(property.price.properties.pyeongPrice.isNotNull()
            .and(property.price.properties.pyeongPrice.goe(filter.getMinPyengPrice())));
      }
    }
    if (filter.getMaxPyengPrice() != null) {
      // 최대값은 null을 0으로 취급하여 항상 포함
      builder.and(property.price.properties.pyeongPrice.isNull()
          .or(property.price.properties.pyeongPrice.loe(filter.getMaxPyengPrice())));
    }

    // 보증금 범위 - null을 0으로 취급
    if (filter.getMinDepositPrice() != null) {
      if (filter.getMinDepositPrice() <= 0) {
        builder.and(property.price.properties.depositPrice.isNull()
            .or(property.price.properties.depositPrice.goe(filter.getMinDepositPrice())));
      } else {
        builder.and(property.price.properties.depositPrice.isNotNull()
            .and(property.price.properties.depositPrice.goe(filter.getMinDepositPrice())));
      }
    }
    if (filter.getMaxDepositPrice() != null) {
      builder.and(property.price.properties.depositPrice.isNull()
          .or(property.price.properties.depositPrice.loe(filter.getMaxDepositPrice())));
    }

    // 월임대료 범위 - null을 0으로 취급
    if (filter.getMinMonthPrice() != null) {
      if (filter.getMinMonthPrice() <= 0) {
        builder.and(property.price.properties.monthPrice.isNull()
            .or(property.price.properties.monthPrice.goe(filter.getMinMonthPrice())));
      } else {
        builder.and(property.price.properties.monthPrice.isNotNull()
            .and(property.price.properties.monthPrice.goe(filter.getMinMonthPrice())));
      }
    }
    if (filter.getMaxMonthPrice() != null) {
      builder.and(property.price.properties.monthPrice.isNull()
          .or(property.price.properties.monthPrice.loe(filter.getMaxMonthPrice())));
    }

    // 관리비 범위 - null을 0으로 취급
    if (filter.getMinGrprice() != null) {
      if (filter.getMinGrprice() <= 0) {
        builder.and(property.price.properties.grPrice.isNull()
            .or(property.price.properties.grPrice.goe(filter.getMinGrprice())));
      } else {
        builder.and(property.price.properties.grPrice.isNotNull()
            .and(property.price.properties.grPrice.goe(filter.getMinGrprice())));
      }
    }
    if (filter.getMaxGrprice() != null) {
      builder.and(property.price.properties.grPrice.isNull()
          .or(property.price.properties.grPrice.loe(filter.getMaxGrprice())));
    }

    // 관리비 지출 범위 - null을 0으로 취급
    if (filter.getMinGrout() != null) {
      if (filter.getMinGrout() <= 0) {
        builder.and(property.price.properties.grOut.isNull()
            .or(property.price.properties.grOut.goe(filter.getMinGrout())));
      } else {
        builder.and(property.price.properties.grOut.isNotNull()
            .and(property.price.properties.grOut.goe(filter.getMinGrout())));
      }
    }
    if (filter.getMaxGrout() != null) {
      builder.and(property.price.properties.grOut.isNull()
          .or(property.price.properties.grOut.loe(filter.getMaxGrout())));
    }

    // 수익률 범위 - null을 0으로 취급
    if (filter.getMinRoi() != null) {
      builder.and(property.price.properties.roi.coalesce(Double.valueOf(0.0)).goe(filter.getMinRoi()));
    }
    if (filter.getMaxRoi() != null) {
      builder.and(property.price.properties.roi.coalesce(Double.valueOf(0.0)).loe(filter.getMaxRoi()));
    }
  }

  public void addAreaRangeFilters(BooleanBuilder builder, AreaFilter options) {

    //  토지면적 (평) - Land 정보에서 조회 - null이 아니면 적용 (0 포함)
    if (options.getMinAreaPyeng() != null || options.getMaxAreaPyeng() != null) {
      addLandAreaFilter(builder, options.getMinAreaPyeng(), options.getMaxAreaPyeng());
    }

    //  연면적 (평) - Ledge 정보에서 조회 - null이 아니면 적용 (0 포함)
    if (options.getMinYeonAreaPyeng() != null || options.getMaxYeonAreaPyeng() != null) {
      addLedgeYeonAreaPyengFilter(builder, options.getMinYeonAreaPyeng(), options.getMaxYeonAreaPyeng());
    }

    //  건축면적 (평) - Ledge 정보에서 조회 - null이 아니면 적용 (0 포함)
    if (options.getMinBuildingAreaPyeng() != null || options.getMaxBuildingAreaPyeng() != null) {
      addLedgeBuildingAreaPyengFilter(builder, options.getMinBuildingAreaPyeng(), options.getMaxBuildingAreaPyeng());
    }

    //  용적률연면적 (평) - Ledge 정보에서 조회 - null이 아니면 적용 (0 포함)
    if (options.getMinYongjeokAreaPyeong() != null || options.getMaxYongjeokAreaPyeong() != null) {
      addLedgeYoungJeokPyengFilter(builder, options.getMinYongjeokAreaPyeong(), options.getMaxYongjeokAreaPyeong());
    }
  }

  public void addLandAreaFilter(BooleanBuilder builder, Double minArea, Double maxArea) {
    QLandInformation land = QLandInformation.landInformation;

    BooleanBuilder landAreaBuilder = new BooleanBuilder();

    if (minArea != null) {
      landAreaBuilder.and(land.properties.areaPyung.goe(minArea));
    }
    if (maxArea != null) {
      landAreaBuilder.and(land.properties.areaPyung.loe(maxArea));
    }

    if (landAreaBuilder.hasValue()) {
      BooleanExpression landAreaExists = JPAExpressions
          .selectOne()
          .from(land)
          .where(
              land.in(property.land)
                  .and(landAreaBuilder)
          )
          .exists();

      builder.and(landAreaExists);
    }
  }

  public BooleanExpression searchInSecret(String query) {
    return JPAExpressions
        .selectOne()
        .from(secret)
        .where(secret.content.containsIgnoreCase(query)
            .and(secret.property.id.eq(property.id)))
        .exists();
  }

  public BooleanExpression searchInFloors(String query) {
    QFloorInformation floor = QFloorInformation.floorInformation;

    return JPAExpressions
        .selectOne()
        .from(floor)
        .where(
            floor.in(property.floors)
                .and(floor.properties.upjong.containsIgnoreCase(query))
        )
        .exists();
  }

  public void addLedgeYoungJeokPyengFilter(BooleanBuilder builder, Integer minYongjeokAreaPyeong,
      Integer maxYongjeokAreaPyeong) {
    QLedgeInformation ledge = QLedgeInformation.ledgeInformation;

    BooleanBuilder ledgeAreaBuilder = new BooleanBuilder();

    if (minYongjeokAreaPyeong != null) {
      ledgeAreaBuilder.and(ledge.properties.yongjeokAreaPyeong.goe(minYongjeokAreaPyeong));
    }
    if (maxYongjeokAreaPyeong != null) {
      ledgeAreaBuilder.and(ledge.properties.yongjeokAreaPyeong.loe(maxYongjeokAreaPyeong));
    }

    if (ledgeAreaBuilder.hasValue()) {
      BooleanExpression ledgeAreaExists = JPAExpressions
          .selectOne()
          .from(ledge)
          .where(
              ledge.in(property.ledge)
                  .and(ledgeAreaBuilder)
          )
          .exists();

      builder.and(ledgeAreaExists);
    }
  }



  public void addLedgeBuildingAreaPyengFilter(BooleanBuilder builder, Integer minArea,
      Integer maxArea) {
    QLedgeInformation ledge = QLedgeInformation.ledgeInformation;

    BooleanBuilder ledgeBuildingAreaBuilder = new BooleanBuilder();

    if (minArea != null) {
      ledgeBuildingAreaBuilder.and(ledge.properties.buildingAreaPyeong.goe(minArea));
    }
    if (maxArea != null) {
      ledgeBuildingAreaBuilder.and(ledge.properties.buildingAreaPyeong.loe(maxArea));
    }

    if (ledgeBuildingAreaBuilder.hasValue()) {
      BooleanExpression ledgeBuildingAreaExists = JPAExpressions
          .selectOne()
          .from(ledge)
          .where(
              ledge.in(property.ledge)
                  .and(ledgeBuildingAreaBuilder)
          )
          .exists();

      builder.and(ledgeBuildingAreaExists);
    }
  }
  public void addLedgeYeonAreaPyengFilter(BooleanBuilder builder, Integer minArea,
      Integer maxArea) {
    QLedgeInformation ledge = QLedgeInformation.ledgeInformation;

    BooleanBuilder ledgeAreaBuilder = new BooleanBuilder();

    if (minArea != null) {
      ledgeAreaBuilder.and(ledge.properties.yeonAreaPyeong.goe(minArea));
    }
    if (maxArea != null) {
      ledgeAreaBuilder.and(ledge.properties.yeonAreaPyeong.loe(maxArea));
    }

    if (ledgeAreaBuilder.hasValue()) {
      BooleanExpression ledgeAreaExists = JPAExpressions
          .selectOne()
          .from(ledge)
          .where(
              ledge.in(property.ledge)
                  .and(ledgeAreaBuilder)
          )
          .exists();

      builder.and(ledgeAreaExists);
    }
  }

  public OrderSpecifier<?> createOrderSpecifier(SortType sortType, SortDirection sortDirection) {
    if (sortType == null) {
      return property.createdAt.desc(); // 기본 정렬
    }

    return switch (sortType) {
      case UPDATED -> sortDirection == SortDirection.ASC
          ? property.updatedAt.asc()
          : property.updatedAt.desc();
      case CREATED -> sortDirection == SortDirection.ASC
          ? property.createdAt.asc()
          : property.createdAt.desc();
      case PRICE -> sortDirection == SortDirection.ASC
          ? property.price.properties.mmPrice.asc()
          : property.price.properties.mmPrice.desc();
      case ROI -> sortDirection == SortDirection.ASC
          ? property.price.properties.roi.asc()
          : property.price.properties.roi.desc();
      case BUILDING_NAME -> sortDirection == SortDirection.ASC
          ? property.buildingName.asc()
          : property.buildingName.desc();
      case ID -> sortDirection == SortDirection.ASC
          ? property.id.asc()
          : property.id.desc();
      case AREA -> createAreaOrderSpecifier(sortDirection);

    };
  }

  public OrderSpecifier<?> createAreaOrderSpecifier(SortDirection sortDirection) {
    NumberExpression<Double> firstLandAreaPyung = Expressions.numberTemplate(Double.class,
        "(SELECT li.properties.areaPyung FROM LandInformation li " +
            "WHERE li IN (SELECT l FROM Property p JOIN p.land l WHERE p.id = {0}) " +
            "ORDER BY li.buildingOrder ASC LIMIT 1)",
        property.id);

    return sortDirection == SortDirection.ASC
        ? firstLandAreaPyung.asc()
        : firstLandAreaPyung.desc();
  }
  public OrderSpecifier<?> createOrderSpecifier(PropertySortType sortType) {
    if (sortType == null) {
      return property.createdAt.desc();
    }
    return switch (sortType) {
      case LATEST -> property.createdAt.desc();
      case OLDEST -> property.createdAt.asc();
      case PRICE_HIGH -> property.price.properties.mmPrice.desc();
      case PRICE_LOW -> property.price.properties.mmPrice.asc();
      case AREA_HIGH -> createAreaOrderSpecifier(SortDirection.DESC);
      case AREA_LOW -> createAreaOrderSpecifier(SortDirection.ASC);
    };
  }


  public BooleanBuilder buildListPredicatesSeller(PropertyMapListReq options, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds, Member member) {
    {
      BooleanBuilder builder = new BooleanBuilder();

      // 지도 좌표 범위 필터링
      if (options.getLtLat() != null && options.getLtLng() != null &&
          options.getRbLat() != null && options.getRbLng() != null) {
        builder.and(property.address.properties.lat.between(options.getRbLat(), options.getLtLat()))
            .and(property.address.properties.lng.between(options.getLtLng(), options.getRbLng()));
      }
      //대분류
      if (bigCategoryIds != null && !bigCategoryIds.isEmpty()) {
        builder.and(property.bigCategory.id.in(bigCategoryIds));
      }

      // 소분류
      if (smallCategoryIds != null && !smallCategoryIds.isEmpty()) {
        builder.and(property.smallCategories.any().id.in(smallCategoryIds));
      }

      // 담당자
      if (adminIds != null && !adminIds.isEmpty()) {
        builder.and(property.adminId.in(adminIds));
      }


      // 진행 상태필터
      if (options.getStatus() != null) {
        switch (options.getStatus()) {
          case READY -> builder.and(property.status.eq(PropertyStatus.READY));
          case COMPLETE -> builder.and(property.status.eq(PropertyStatus.COMPLETE));
          case PENDING -> builder.and(property.status.eq(PropertyStatus.PENDING));
          case SOLD -> builder.and(property.status.eq(PropertyStatus.SOLD));
        }
      }

      // 시도/시군구/법정동 필터 - LIKE 패턴으로 최적화
      if (options.getSidoCode() != null) {
        if (options.getSigungnCode() != null) {
          if (options.getBjdCode() != null) {
            String pnuPrefix = options.getSidoCode() + options.getSigungnCode() + options.getBjdCode();
            builder.and(property.address.properties.pnu.like(pnuPrefix + "%"));
          } else {
            String pnuPrefix = options.getSidoCode() + options.getSigungnCode();
            builder.and(property.address.properties.pnu.like(pnuPrefix + "%"));
          }
        } else {
          String pnuPrefix = options.getSidoCode();
          builder.and(property.address.properties.pnu.like(pnuPrefix + "%"));
        }
      }


      //공개 비공개
      if (options.getIsPublic() != null) {
        builder.and(property.isPublic.eq(options.getIsPublic()));
      }

      //지번 필터 검색
      jibunFilter(options.getMinJibun(),options.getMaxJibun(),builder);

      //가격 관련 필터
      addPriceRangeFilters(builder, options);

      //면적 관련 필터
      addAreaRangeFilters(builder, options);

      return builder;
    }
  }
}
