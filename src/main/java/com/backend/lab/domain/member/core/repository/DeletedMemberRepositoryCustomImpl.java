package com.backend.lab.domain.member.core.repository;

import com.backend.lab.domain.member.core.entity.DeletedMember;
import com.backend.lab.domain.member.core.entity.QDeletedMember;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerSearchOptions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class DeletedMemberRepositoryCustomImpl implements DeletedMemberRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QDeletedMember qMember = QDeletedMember.deletedMember;

  @Override
  public Page<DeletedMember> searchCustomer(AdminCustomerSearchOptions options) {
    BooleanBuilder builder = new BooleanBuilder();

    if (options.getAdminId() != null) {
      builder.and(qMember.customerProperties.adminId.eq(options.getAdminId()));
    }

    if (options.getCompanyType() != null) {
      builder.and(qMember.sellerProperties.adminId.eq(options.getAdminId()));
    }

    List<DeletedMember> rows = queryFactory.selectFrom(qMember)
        .where(builder)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(qMember.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(qMember.count())
        .from(qMember)
        .where(builder)
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }
}
