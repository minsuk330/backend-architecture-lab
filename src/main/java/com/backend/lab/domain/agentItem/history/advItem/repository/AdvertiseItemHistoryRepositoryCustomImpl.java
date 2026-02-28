package com.backend.lab.domain.agentItem.history.advItem.repository;

import com.backend.lab.domain.agentItem.history.advItem.entity.AdvertiseItemHistory;
import com.backend.lab.domain.agentItem.history.advItem.entity.QAdvertiseItemHistory;
import com.backend.lab.domain.agentItem.history.advItem.entity.dto.AdvertiseHistorySearchOptions;
import com.backend.lab.domain.propertyAdvertisement.entity.QPropertyAdvertisement;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class AdvertiseItemHistoryRepositoryCustomImpl implements
    AdvertiseItemHistoryRepositoryCustom {

  private final JPQLQueryFactory queryFactory;
  private final QAdvertiseItemHistory itemHistory = QAdvertiseItemHistory.advertiseItemHistory;
  private final QPropertyAdvertisement propertyAdvertisement = QPropertyAdvertisement.propertyAdvertisement;

  @Override
  public Page<AdvertiseItemHistory> search(AdvertiseHistorySearchOptions options) {

    BooleanBuilder builder = new BooleanBuilder();

    // 삭제되지 않은 PropertyAdvertisement만 조회
    builder.and(itemHistory.propertyAdvertisement.deletedAt.isNull());

    String query = options.getQuery();
    if (query != null && !query.isEmpty()) {
      builder.and(
          itemHistory.id.stringValue().containsIgnoreCase(query)
              .or(itemHistory.propertyAdvertisement.agent.agentProperties.businessName.contains(
                  query))
              .or(itemHistory.propertyAdvertisement.agent.id.stringValue()
                  .containsIgnoreCase(query))
      );
    }

    Pageable pageable = options.pageable();

    List<AdvertiseItemHistory> rows = queryFactory
        .selectFrom(itemHistory)
        .where(builder)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(itemHistory.count())
        .from(itemHistory)
        .where(builder)
        .fetchOne();

    return new PageImpl<>(rows, pageable, total == null ? 0 : total);
  }
}
