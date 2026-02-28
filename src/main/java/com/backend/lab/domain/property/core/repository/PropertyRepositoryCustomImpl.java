package com.backend.lab.domain.property.core.repository;


import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyStatReq;
import com.backend.lab.api.admin.property.core.dto.req.StatFilterable;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatResp;
import com.backend.lab.api.admin.property.core.dto.resp.StatisticResp;
import com.backend.lab.api.admin.wishlist.dto.req.SearchWishlistOptions;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.api.member.common.property.core.dto.req.PropertyMemberOptions;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.admin.core.entity.QAdmin;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.QMember;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.QProperty;
import com.backend.lab.domain.property.core.entity.vo.PropertyStatus;
import com.backend.lab.domain.propertyMember.entity.QPropertyMember;
import com.backend.lab.domain.secret.entity.QSecret;
import com.backend.lab.domain.wishlist.entity.QWishlist;
import com.backend.lab.domain.wishlistGroup.entity.QWishlistGroup;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PropertyRepositoryCustomImpl implements PropertyRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final EntityManager entityManager;
  private final QProperty property = QProperty.property;
  private final QSecret secret = QSecret.secret;
  private final QPropertyMember propertyMember = QPropertyMember.propertyMember;
  private final QMember member = QMember.member;
  private final QWishlist wishlist = QWishlist.wishlist;
  private final QAdmin admin = QAdmin.admin;

  private final PropertyRepositoryHelper repositoryHelper;

  @Override
  public Page<Property> search(SearchPropertyOptions options, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {

    BooleanBuilder predicates = repositoryHelper.buildSearchPredicates(options, adminIds,
        bigCategoryIds, smallCategoryIds);
    OrderSpecifier<?> orderSpecifier = repositoryHelper.createOrderSpecifier(options.getSortType(),
        options.getSortDirection());

    List<Property> rows = queryFactory
        .selectFrom(property)
        .distinct()
        .leftJoin(property.price).fetchJoin()
        .leftJoin(property.address).fetchJoin()
        .leftJoin(property.bigCategory).fetchJoin()
        .leftJoin(property.smallCategories)
        .where(
            property.deletedAt.isNull()
                .and(predicates)
        )
        .orderBy(orderSpecifier)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    Long total = queryFactory
        .select(property.count())
        .from(property)
        .where(
            property.deletedAt.isNull()
                .and(predicates)
        )
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  @Override
  public Page<Property> memberWishlist(PageOptions options, Long memberId) {

    List<Property> properties = queryFactory
        .selectFrom(property)
        .join(wishlist).on(wishlist.property.id.eq(property.id))
        .leftJoin(property.price).fetchJoin()
        .leftJoin(property.address).fetchJoin()
        .where(
            property.deletedAt.isNull()
                .and(wishlist.member.id.eq(memberId))
        )
        .orderBy(wishlist.createdAt.desc())
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    Long total = queryFactory
        .select(property.count())
        .from(property)
        .join(wishlist).on(wishlist.property.id.eq(property.id))
        .where(
            property.deletedAt.isNull()
                .and(wishlist.member.id.eq(memberId))
        )
        .fetchOne();

    return new PageImpl<>(properties, options.pageable(), total != null ? total : 0);
  }

  @Override
  public PropertyStatResp getStat(PropertyStatReq req) {

    BooleanBuilder totalCondition = buildWhereClause(req.getTotal(), false);
    BooleanBuilder monthlyCondition = buildWhereClause(req.getMonthly(), true);

    BooleanBuilder baseCondition = new BooleanBuilder();

    baseCondition.and(property.deletedAt.isNull());
    
    BooleanBuilder todayConditionNoFilter = new BooleanBuilder(baseCondition);
    BooleanBuilder yesterdayConditionNoFilter = new BooleanBuilder(baseCondition);
    BooleanBuilder monthlyConditionNoFilter = new BooleanBuilder(baseCondition);

    // 날짜 조건 추가 (필터 없이)
    LocalDateTime now = LocalDateTime.now();
    
    // 이번달 날짜 조건을 monthlyCondition에 추가
    LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
    monthlyCondition.and(property.createdAt.between(startOfMonth, endOfMonth));

    // 오늘
    LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
    todayConditionNoFilter.and(property.createdAt.between(startOfDay, endOfDay));

    // 어제
    LocalDateTime startOfYesterday = now.minusDays(1).withHour(0).withMinute(0).withSecond(0)
        .withNano(0);
    LocalDateTime endOfYesterday = startOfYesterday.plusDays(1).minusNanos(1);
    yesterdayConditionNoFilter.and(property.createdAt.between(startOfYesterday, endOfYesterday));

    // 이번달
    monthlyConditionNoFilter.and(property.createdAt.between(startOfMonth, endOfMonth));

    // 전체 통계 (상태별) - 필터 적용
    Long totalReady = getCountByStatus(totalCondition, PropertyStatus.READY);
    Long totalComplete = getCountByStatus(totalCondition, PropertyStatus.COMPLETE);
    Long totalPending = getCountByStatus(totalCondition, PropertyStatus.PENDING);
    Long totalSold = getCountByStatus(totalCondition, PropertyStatus.SOLD);

    // 당월 통계 (상태별) - 필터 적용
    Long monthlyReady = getCountByStatus(monthlyCondition, PropertyStatus.READY);
    Long monthlyComplete = getCountByStatus(monthlyCondition, PropertyStatus.COMPLETE);
    Long monthlyPending = getCountByStatus(monthlyCondition, PropertyStatus.PENDING);
    Long monthlySold = getCountByStatus(monthlyCondition, PropertyStatus.SOLD);

    // 추가 통계 (전체 개수) - 필터 적용
    Long monthlyPropertyCount = getTotalCount(monthlyCondition);    
    Long todayPropertyCount = getTotalCount(todayConditionNoFilter);
    Long totalPropertyCount = getTotalCount(baseCondition);
    Long yesterdayPropertyCount = getTotalCount(yesterdayConditionNoFilter);

    StatisticResp total = StatisticResp.builder()
        .ready(totalReady)
        .complete(totalComplete)
        .pending(totalPending)
        .sold(totalSold)
        .build();

    StatisticResp monthly = StatisticResp.builder()
        .ready(monthlyReady)
        .complete(monthlyComplete)
        .pending(monthlyPending)
        .sold(monthlySold)
        .build();

    return PropertyStatResp.builder()
        .monthlyPropertyCount(monthlyPropertyCount)
        .toDayPropertyCount(todayPropertyCount)
        .totalPropertyCount(totalPropertyCount)
        .yesterdayPropertyCount(yesterdayPropertyCount)
        .total(total)
        .monthly(monthly)
        .build();
  }

  private Long getCountByStatus(BooleanBuilder condition, PropertyStatus status) {

    //해결
    BooleanBuilder newCondition = new BooleanBuilder(condition);

    return queryFactory
        .select(property.count())
        .from(property)
        .where(newCondition.and(property.status.eq(status)))
        .fetchOne();
  }

  // 전체 개수 카운트
  private Long getTotalCount(BooleanBuilder condition) {
    return queryFactory
        .select(property.count())
        .from(property)
        .where(condition)
        .fetchOne();
  }

  private BooleanBuilder buildWhereClause(StatFilterable filter, boolean isMonthly) {
    BooleanBuilder builder = new BooleanBuilder();
    
    // 기본 조건: 삭제되지 않은 매물만
    builder.and(property.deletedAt.isNull());

    if (filter != null) {
      // 부서 필터
      if (filter.getDepartmentId() != null) {
        List<Long> adminIds = queryFactory
            .select(admin.id)
            .from(admin)
            .where(admin.department.id.eq(filter.getDepartmentId())
                .and(admin.deletedAt.isNull())) // admin도 삭제되지 않은 것만
            .fetch();

        if (!adminIds.isEmpty()) {
          builder.and(property.adminId.in(adminIds));
        } else {
          // 해당 부서에 admin이 없으면 결과 없음
          builder.and(property.id.eq(-1L));
        }
      }

      // 대분류 필터
      if (filter.getMajorCategoryId() != null) {
        builder.and(property.bigCategory.id.eq(filter.getMajorCategoryId()));
      }

      // 직원 필터
      if (filter.getAdminId() != null) {
        builder.and(property.adminId.eq(filter.getAdminId()));
      }


      return builder;
    }
    return builder;
  }


  public Page<Property> getsWithPropertyMember(Long memberId, Pageable pageable) {
    BooleanBuilder builder = new BooleanBuilder();
    builder.and(
        propertyMember.memberId.eq(memberId)
    );
    List<Property> rows = queryFactory
        .selectFrom(property)
        .leftJoin(propertyMember).on(propertyMember.propertyId.eq(property.id))
        .where(builder)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(property.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(property.count())
        .from(property)
        .leftJoin(propertyMember).on(propertyMember.propertyId.eq(property.id))
        .where(builder)
        .fetchOne();
    return new PageImpl<>(rows, pageable, total != null ? total : 0);
  }

  @Override
  public List<Property> getsByMapWithSeller(PropertyMapListReq req, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds, Member member) {
    BooleanExpression seller = (propertyMember.memberId.eq(member.getId()));

    OrderSpecifier<?> orderSpecifier = repositoryHelper.createOrderSpecifier(req.getSortType());
    BooleanBuilder finalBuilder = new BooleanBuilder();

    // 메뉴 타입 조건
    BooleanBuilder menuBuilder = repositoryHelper.buildMenuTypesPredicate(req.getMenuTypes());
    if (menuBuilder.hasValue()) {
      finalBuilder.and(menuBuilder);
    }

    // 리스트 조건들
    BooleanBuilder listBuilder = repositoryHelper.buildListPredicatesSeller(req, adminIds, bigCategoryIds,
        smallCategoryIds,member);
    if (listBuilder.hasValue()) {
      finalBuilder.and(listBuilder);
    }

    List<Property> rows = queryFactory.selectFrom(property)
        .distinct()
        .leftJoin(property.price).fetchJoin()
        .leftJoin(propertyMember).on(propertyMember.propertyId.eq(property.id))
        .leftJoin(property.address).fetchJoin()
        .leftJoin(property.bigCategory).fetchJoin()
        .leftJoin(property.smallCategories)
        .where(finalBuilder
            .or(seller))
        .orderBy(orderSpecifier)
        .fetch();

    return rows;


  }


  public List<Property> getsByMap(PropertyMapListReq req, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    OrderSpecifier<?> orderSpecifier = repositoryHelper.createOrderSpecifier(req.getSortType());
    BooleanBuilder finalBuilder = new BooleanBuilder();

    // 메뉴 타입 조건
    BooleanBuilder menuBuilder = repositoryHelper.buildMenuTypesPredicate(req.getMenuTypes());
    if (menuBuilder.hasValue()) {
      finalBuilder.and(menuBuilder);
    }

    // 리스트 조건들
    BooleanBuilder listBuilder = repositoryHelper.buildListPredicates(req, adminIds, bigCategoryIds,
        smallCategoryIds);
    if (listBuilder.hasValue()) {
      finalBuilder.and(listBuilder);
    }

    // 필요한 연관관계만 fetchJoin + 결과 제한
    List<Property> rows = queryFactory.selectFrom(property)
        .leftJoin(property.price).fetchJoin()
        .leftJoin(property.address).fetchJoin()
        .leftJoin(property.thumbnailImageUrl).fetchJoin()
        .where(finalBuilder)
        .orderBy(orderSpecifier)
        .fetch();

    return rows;

  }

  @Override
  public Page<Property> findBynonMember(PropertyMemberOptions options) {
    BooleanBuilder predicates = repositoryHelper.buildMenuTypesPredicate(options.getMenuTypes());
    OrderSpecifier<?> orderSpecifier = repositoryHelper.createOrderSpecifier(options.getSortType());

    List<Property> rows = queryFactory
        .selectFrom(property)
        .leftJoin(property.price).fetchJoin()
        .leftJoin(property.address).fetchJoin()
        .leftJoin(property.bigCategory).fetchJoin()
        .leftJoin(property.smallCategories)
        .where(
            property.deletedAt.isNull()
                .and(property.isPublic.isTrue()) // 공개된 매물만
                .and(property.status.eq(PropertyStatus.COMPLETE)) // 완료 상태만
                .and(predicates)
        )
        .orderBy(orderSpecifier)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    Long total = queryFactory
        .select(property.count())
        .from(property)
        .where(
            property.deletedAt.isNull()
                .and(property.isPublic.isTrue())
                .and(property.status.eq(PropertyStatus.COMPLETE))
                .and(predicates)
        )
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  @Override
  public Page<Property> findBySeller(Long sellerId, PropertyMemberOptions options) {

    BooleanBuilder predicates = repositoryHelper.buildMenuTypesPredicate(options.getMenuTypes());
    OrderSpecifier<?> orderSpecifier = repositoryHelper.createOrderSpecifier(options.getSortType());

    // 본인이 등록한 매물 또는 공개된 완료 매물
    BooleanExpression sellerOrPublic = property.status.eq(PropertyStatus.COMPLETE)
        .or(propertyMember.memberId.eq(sellerId));

    List<Property> rows = queryFactory
        .selectFrom(property)
        .leftJoin(propertyMember).on(propertyMember.propertyId.eq(property.id))
        .leftJoin(member).on(propertyMember.memberId.eq(member.id))
        .leftJoin(property.price).fetchJoin()
        .leftJoin(property.address).fetchJoin()
        .leftJoin(property.thumbnailImageUrl).fetchJoin()
        .where(
            property.deletedAt.isNull()
                .and(sellerOrPublic)
                .and(predicates)
        )
        .orderBy(orderSpecifier)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    Long total = queryFactory
        .select(property.count())
        .from(property)
        .leftJoin(propertyMember).on(propertyMember.propertyId.eq(property.id))
        .leftJoin(member).on(propertyMember.memberId.eq(member.id))
        .where(
            property.deletedAt.isNull()
                .and(sellerOrPublic)
                .and(predicates)
        )
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  @Override
  public Page<Property> findByBuyer(PropertyMemberOptions options) {

    BooleanBuilder predicates = repositoryHelper.buildMenuTypesPredicate(options.getMenuTypes());
    OrderSpecifier<?> orderSpecifier = repositoryHelper.createOrderSpecifier(options.getSortType());

    BooleanExpression booleanExpression = property.status.eq(PropertyStatus.COMPLETE);

    List<Property> rows = queryFactory
        .selectFrom(property)
        .leftJoin(property.price).fetchJoin()
        .leftJoin(property.address).fetchJoin()
        .leftJoin(property.bigCategory).fetchJoin()
        .leftJoin(property.smallCategories)
        .where(
            property.deletedAt.isNull()
                .and(booleanExpression)
                .and(predicates)
        )
        .orderBy(orderSpecifier)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    Long total = queryFactory
        .select(property.count())
        .from(property)
        .leftJoin(propertyMember).on(propertyMember.propertyId.eq(property.id))
        .leftJoin(member).on(propertyMember.memberId.eq(member.id))
        .where(
            property.deletedAt.isNull()
                .and(booleanExpression)
                .and(predicates)
        )
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  @Override
  public Page<Property> findByAgent(PropertyMemberOptions options) {

    BooleanBuilder predicates = repositoryHelper.buildMenuTypesPredicate(options.getMenuTypes());
    OrderSpecifier<?> orderSpecifier = repositoryHelper.createOrderSpecifier(options.getSortType());

    // 공인중개사는 모든 매물 조회 가능
    List<Property> rows = queryFactory
        .selectFrom(property)
        .leftJoin(property.price).fetchJoin()
        .leftJoin(property.address).fetchJoin()
        .leftJoin(property.bigCategory).fetchJoin()
        .leftJoin(property.smallCategories)
        .where(
            property.deletedAt.isNull()
                .and(predicates)
        )
        .orderBy(orderSpecifier)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    Long total = queryFactory
        .select(property.count())
        .from(property)
        .where(
            property.deletedAt.isNull()
                .and(predicates)
        )
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }


  @Override
  public Page<Property> wishlistSearch(SearchWishlistOptions options) {
    BooleanBuilder predicates = repositoryHelper.buildWishlistSearchPredicates(options);

    QWishlistGroup wishlistGroup = QWishlistGroup.wishlistGroup;
    predicates.and(JPAExpressions
        .selectOne()
        .from(wishlistGroup)
        .where(
            wishlistGroup.minInterestCount.loe(property.wishCount)
                .and(wishlistGroup.maxInterestCount.goe(property.wishCount))
        )
        .exists());

    List<Property> rows = queryFactory.selectFrom(property)
        .leftJoin(property.price).fetchJoin()
        .leftJoin(property.address).fetchJoin()
        .leftJoin(property.bigCategory).fetchJoin()
        .leftJoin(property.smallCategories)
        .where(predicates)
        .orderBy(property.createdAt.desc())
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    Long total = queryFactory
        .select(property.count())
        .from(property)
        .where(predicates)
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

}