package com.backend.lab.domain.member.memberWorkLog.repository;

import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogDetailOptions;
import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogDetailResp;
import com.backend.lab.api.admin.manageLog.dto.MemberWorkLogResp;
import com.backend.lab.api.admin.manageLog.dto.SearchMemberWorkLogOptions;
import com.backend.lab.domain.admin.core.entity.QAdmin;
import com.backend.lab.domain.member.core.entity.QMember;
import com.backend.lab.domain.member.memberWorkLog.entity.QMemberWorkLog;
import com.backend.lab.domain.member.memberWorkLog.entity.QMemberWorkLogDetail;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class MemberWorkLogRepositoryCustomImpl implements MemberWorkLogRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QMemberWorkLog memberWorkLog = QMemberWorkLog.memberWorkLog;
  private final QMember member = QMember.member;
  private final QAdmin admin = QAdmin.admin;
  QMemberWorkLogDetail detail = QMemberWorkLogDetail.memberWorkLogDetail;

  @Override
  public Page<MemberWorkLogResp> gets(SearchMemberWorkLogOptions options) {
    // 프로젝션을 사용하여 직접 DTO로 조회
    List<MemberWorkLogResp> content = queryFactory
        .select(Projections.constructor(MemberWorkLogResp.class,
            memberWorkLog.id,
            memberWorkLog.createdAt,
            memberWorkLog.updatedAt,
            memberWorkLog.workLogType,
            member.customerProperties.companyType,
            member.id,
            member.customerProperties.name,
            member.customerProperties.phoneNumber,
            admin.name,
            memberWorkLog.adminIp
        ))
        .from(memberWorkLog)
        .leftJoin(memberWorkLog.member, member)
        .leftJoin(memberWorkLog.createdBy, admin)
        .where(memberWorkLog.deletedAt.isNull())
        .orderBy(memberWorkLog.createdAt.desc())
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    // 총 개수 조회
    Long total = queryFactory
        .select(memberWorkLog.count())
        .from(memberWorkLog)
        .where(memberWorkLog.deletedAt.isNull())
        .fetchOne();

    return new PageImpl<>(content, options.pageable(), total != null ? total : 0);
  }

  @Override
  public Page<MemberWorkLogDetailResp> findDetails(MemberWorkLogDetailOptions options) {

    // 프로젝션을 사용하여 직접 DTO로 조회
    List<MemberWorkLogDetailResp> content = queryFactory
        .select(Projections.constructor(MemberWorkLogDetailResp.class,
            detail.id,
            detail.createdAt,
            detail.updatedAt,
            detail.logFieldType,
            memberWorkLog.workLogType,
            detail.beforeValue,
            detail.afterValue,
            admin.name
        ))
        .from(detail)
        .leftJoin(detail.memberWorkLog, memberWorkLog)
        .leftJoin(memberWorkLog.createdBy, admin)
        .where(
            detail.memberWorkLog.id.eq(options.getWorkLogId())
                .and(detail.deletedAt.isNull())
        )
        .orderBy(detail.createdAt.desc())
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    // 총 개수 조회
    Long total = queryFactory
        .select(detail.count())
        .from(detail)
        .where(
            detail.memberWorkLog.id.eq(options.getWorkLogId())
                .and(detail.deletedAt.isNull())
        )
        .fetchOne();

    return new PageImpl<>(content, options.pageable(), total != null ? total : 0);
  }
}
