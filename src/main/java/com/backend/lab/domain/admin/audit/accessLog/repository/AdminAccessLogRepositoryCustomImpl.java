package com.backend.lab.domain.admin.audit.accessLog.repository;

import com.backend.lab.domain.admin.audit.accessLog.entity.dto.req.GetAdminAccessLogOptions;
import com.backend.lab.domain.admin.audit.accessLog.entity.AdminAccessLog;
import com.backend.lab.domain.admin.audit.accessLog.entity.QAdminAccessLog;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class AdminAccessLogRepositoryCustomImpl implements AdminAccessLogRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QAdminAccessLog adminAccessLog = QAdminAccessLog.adminAccessLog;

  @Override
  public Page<AdminAccessLog> findLatestLogsEachUser(Pageable pageable) {
    QAdminAccessLog subLog = new QAdminAccessLog("subLog");

    List<AdminAccessLog> rows = queryFactory
        .selectFrom(adminAccessLog)
        .where(
            adminAccessLog.createdAt.eq(
                JPAExpressions
                    .select(subLog.createdAt.max())
                    .from(subLog)
                    .where(subLog.adminId.eq(adminAccessLog.adminId))
            )
        )
        .orderBy(adminAccessLog.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(adminAccessLog.count())
        .from(adminAccessLog)
        .where(
            adminAccessLog.createdAt.eq(
                JPAExpressions
                    .select(subLog.createdAt.max())
                    .from(subLog)
                    .where(subLog.adminId.eq(adminAccessLog.adminId))
            )
        )
        .fetchOne();
    return new PageImpl<>(rows, pageable, total != null ? total : 0);
  }

  @Override
  public Page<AdminAccessLog> search(GetAdminAccessLogOptions options) {

    BooleanBuilder predicate = this.getSearchPredicate(options);

    List<AdminAccessLog> rows = queryFactory
        .selectFrom(adminAccessLog)
        .where(predicate)
        .orderBy(adminAccessLog.createdAt.desc())
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    Long total = queryFactory
        .select(adminAccessLog.count())
        .from(adminAccessLog)
        .where(predicate)
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  private BooleanBuilder getSearchPredicate(GetAdminAccessLogOptions options) {
    BooleanBuilder predicate = new BooleanBuilder();

    if (options.getAdminIds() != null && !options.getAdminIds().isEmpty()) {
      predicate.and(adminAccessLog.adminId.in(options.getAdminIds()));
    }

    if (options.getStartDate() != null && options.getEndDate() != null) {
      predicate.and(adminAccessLog.createdAt.between(options.getStartDate(), options.getEndDate()));
    } else if (options.getStartDate() != null) {
      predicate.and(adminAccessLog.createdAt.goe(options.getStartDate()));
    } else if (options.getEndDate() != null) {
      predicate.and(adminAccessLog.createdAt.loe(options.getEndDate()));
    }

    return predicate;
  }
}
