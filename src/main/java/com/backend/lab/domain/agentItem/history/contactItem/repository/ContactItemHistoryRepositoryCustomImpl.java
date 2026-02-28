package com.backend.lab.domain.agentItem.history.contactItem.repository;

import com.backend.lab.domain.agentItem.history.contactItem.entity.ContactItemHistory;
import com.backend.lab.domain.agentItem.history.contactItem.entity.QContactItemHistory;
import com.backend.lab.domain.agentItem.history.contactItem.entity.dto.ContactHistorySearchOptions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ContactItemHistoryRepositoryCustomImpl implements ContactItemHistoryRepositoryCustom{

  private final JPAQueryFactory queryFactory;
  private final QContactItemHistory contactItemHistory= QContactItemHistory.contactItemHistory;

  @Override
  public Page<ContactItemHistory> search(ContactHistorySearchOptions options) {

    BooleanBuilder builder = new BooleanBuilder();

    String query = options.getQuery();
    if (query != null && !query.isEmpty()) {
      builder.and(
          contactItemHistory.id.stringValue().containsIgnoreCase(query)
              .or(contactItemHistory.agent.agentProperties.businessName.containsIgnoreCase(query))
              .or(contactItemHistory.property.id.stringValue().containsIgnoreCase(query))
      );
    }

    Long agentId = options.getAgentId();
    if (agentId != null) {
      builder.and(contactItemHistory.agent.id.eq(agentId));
    }

    LocalDate day = options.getDay();
    if (day != null) {
      LocalDateTime start = day.atStartOfDay();
      LocalDateTime end = start.plusDays(1);
      builder.and(contactItemHistory.createdAt.between(start, end));
    }

    Pageable pageable = options.pageable();

    List<ContactItemHistory> rows = queryFactory
        .selectFrom(contactItemHistory)
        .where(builder)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(contactItemHistory.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(contactItemHistory.count())
        .from(contactItemHistory)
        .where(builder)
        .fetchOne();
    return new PageImpl<>(rows, pageable, total == null ? 0 : total);
  }

}
