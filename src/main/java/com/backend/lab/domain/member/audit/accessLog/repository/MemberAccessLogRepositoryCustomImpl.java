package com.backend.lab.domain.member.audit.accessLog.repository;

import com.backend.lab.domain.member.audit.accessLog.entity.MemberAccessLog;
import com.backend.lab.domain.member.audit.accessLog.entity.QMemberAccessLog;
import com.backend.lab.domain.member.audit.accessLog.entity.dto.req.GetMemberAccessLogOptions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class MemberAccessLogRepositoryCustomImpl implements MemberAccessLogRepositoryCustom{

  private final JPAQueryFactory queryFactory;
  private final QMemberAccessLog memberAccessLog = QMemberAccessLog.memberAccessLog;

  @Override
  public Page<MemberAccessLog> findLatestLogsEachUser(Pageable pageable) {
    QMemberAccessLog subLog = new QMemberAccessLog("subLog");

    List<MemberAccessLog> rows = queryFactory
        .selectFrom(memberAccessLog)
        .where(
            memberAccessLog.createdAt.eq(
                JPAExpressions
                    .select(subLog.createdAt.max())
                    .from(subLog)
                    .where(subLog.memberId.eq(memberAccessLog.memberId))
            )
        )
        .orderBy(memberAccessLog.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(memberAccessLog.count())
        .from(memberAccessLog)
        .where(
            memberAccessLog.createdAt.eq(
                JPAExpressions
                    .select(subLog.createdAt.max())
                    .from(subLog)
                    .where(subLog.memberId.eq(memberAccessLog.memberId))
            )
        )
        .fetchOne();
    return new PageImpl<>(rows, pageable, total != null ? total : 0);
  }

  @Override
  public Page<MemberAccessLog> search(GetMemberAccessLogOptions options) {

    BooleanBuilder predicate = this.getSearchPredicate(options);

    List<MemberAccessLog> rows = queryFactory
        .selectFrom(memberAccessLog)
        .where(predicate)
        .orderBy(memberAccessLog.createdAt.desc())
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    Long total = queryFactory
        .select(memberAccessLog.count())
        .from(memberAccessLog)
        .where(predicate)
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  private BooleanBuilder getSearchPredicate(GetMemberAccessLogOptions options) {
    BooleanBuilder predicate = new BooleanBuilder();

    if (options.getMemberIds() != null && !options.getMemberIds().isEmpty()) {
      predicate.and(memberAccessLog.memberId.in(options.getMemberIds()));
    }

    if (options.getStartDate() != null && options.getEndDate() != null) {
      predicate.and(memberAccessLog.createdAt.between(options.getStartDate(), options.getEndDate()));
    } else if (options.getStartDate() != null) {
      predicate.and(memberAccessLog.createdAt.goe(options.getStartDate()));
    } else if (options.getEndDate() != null) {
      predicate.and(memberAccessLog.createdAt.loe(options.getEndDate()));
    }

    return predicate;
  }
}
