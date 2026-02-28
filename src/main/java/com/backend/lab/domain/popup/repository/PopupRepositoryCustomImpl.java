package com.backend.lab.domain.popup.repository;

import com.backend.lab.domain.popup.entity.Popup;
import com.backend.lab.domain.popup.entity.QPopup;
import com.backend.lab.domain.popup.entity.dto.req.SearchPopupOptions;
import com.backend.lab.domain.popup.entity.vo.PopupViewType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class PopupRepositoryCustomImpl implements PopupRepositoryCustom{

  private final JPAQueryFactory queryFactory;
  private final QPopup popup = QPopup.popup;

  @Override
  public Page<Popup> search(SearchPopupOptions options, PopupViewType viewType) {
    BooleanBuilder predicates = buildSearchPredicates(options, viewType);

    var rows = queryFactory.selectFrom(popup)
        .where(predicates)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(popup.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(popup.count())
        .from(popup)
        .where(predicates)
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  @Override
  public List<Popup> getsAvailable(LocalDate date, PopupViewType viewType) {
    BooleanBuilder predicates = new BooleanBuilder();
    predicates.and(popup.startAt.loe(date))
              .and(popup.endAt.goe(date));

    switch (viewType) {
      case ANONYMOUS:
        predicates.and(popup.canAnonymousView.eq(true));
        break;
      case AGENT:
        predicates.and(popup.canAgentView.eq(true));
        break;
      case BUYER:
        predicates.and(popup.canBuyerView.eq(true));
        break;
      case SELLER:
        predicates.and(popup.canSellerView.eq(true));
        break;
    }

    return queryFactory.selectFrom(popup)
                       .where(predicates)
                       .orderBy(popup.createdAt.desc())
                       .fetch();
  }

  public BooleanBuilder buildSearchPredicates(SearchPopupOptions options, PopupViewType viewType) {
    BooleanBuilder predicates = new BooleanBuilder();

    if(options.getAdminName() != null && !options.getAdminName().isBlank()) {
      predicates.and(popup.createdBy.name.containsIgnoreCase(options.getAdminName()));
    }

    switch (viewType) {
      case ANONYMOUS:
        predicates.and(popup.canAnonymousView.eq(true));
        break;
      case AGENT:
        predicates.and(popup.canAgentView.eq(true));
        break;
      case BUYER:
        predicates.and(popup.canBuyerView.eq(true));
        break;
      case SELLER:
        predicates.and(popup.canSellerView.eq(true));
        break;
    }

    return predicates;
  }
}
