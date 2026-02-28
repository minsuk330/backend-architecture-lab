package com.backend.lab.domain.property.core.repository;

import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.QProperty;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class PropertyDeletedRepositoryCustomImpl implements PropertyDeletedRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final EntityManager entityManager;
  private final QProperty property = QProperty.property;

  private final PropertyRepositoryHelper repositoryHelper;

  @Override
  public Optional<Property> getDeletedById(Long id) {
    return withDeletedFilter(false, () ->
        Optional.ofNullable(
            queryFactory
                .selectFrom(property)
                .where(property.id.eq(id))
                .fetchOne()
        )
    );
  }

  @Override
  public Page<Property> search(SearchPropertyOptions options, List<Long> adminIds,
      List<Long> bigCategoryIds, List<Long> smallCategoryIds) {
    return withDeletedFilter(false, () -> {
      BooleanBuilder predicates = repositoryHelper.buildSearchPredicates(options, adminIds,
          bigCategoryIds, smallCategoryIds);
      OrderSpecifier<?> orderSpecifier = repositoryHelper.createOrderSpecifier(
          options.getSortType(), options.getSortDirection());

      List<Property> rows = queryFactory
          .selectFrom(property)
          .leftJoin(property.price).fetchJoin()
          .leftJoin(property.address).fetchJoin()
          .leftJoin(property.bigCategory).fetchJoin()
          .leftJoin(property.smallCategories)
          .where(predicates)
          .orderBy(orderSpecifier)
          .offset(options.pageable().getOffset())
          .limit(options.pageable().getPageSize())
          .fetch();

      Long total = queryFactory
          .select(property.count())
          .from(property)
          .where(predicates)
          .fetchOne();

      return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
    });
  }

  private <T> T withDeletedFilter(boolean enableFilter, Supplier<T> supplier) {
    Session session = entityManager.unwrap(Session.class);

    if (!enableFilter) {
      session.disableFilter("deletedPropertyFilter");
      session.enableFilter("onlyDeletedPropertyFilter");
    }

    try {
      return supplier.get();
    } finally {
      if (!enableFilter) {
        session.enableFilter("deletedPropertyFilter");
        session.disableFilter("onlyDeletedPropertyFilter");
      }
    }
  }
}
