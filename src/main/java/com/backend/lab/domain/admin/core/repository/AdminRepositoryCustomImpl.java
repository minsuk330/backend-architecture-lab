package com.backend.lab.domain.admin.core.repository;

import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.QAdmin;
import com.backend.lab.domain.admin.core.entity.dto.req.SearchAdminOptions;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class AdminRepositoryCustomImpl implements AdminRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QAdmin qAdmin = QAdmin.admin;

  @Override
  public List<Admin> findAllByIdAndRole(List<Long> ids, AdminRole role) {

    BooleanBuilder predicates = new BooleanBuilder();
    if (ids != null && !ids.isEmpty()) {
      predicates.and(qAdmin.id.in(ids));
    }

    if (role != null) {
      predicates.and(qAdmin.role.eq(role));
    }

    return queryFactory.selectFrom(qAdmin)
        .where(predicates)
        .fetch();
  }

  @Override
  public Page<Admin> search(SearchAdminOptions options) {
    BooleanBuilder predicates = buildSearchPredicates(options);

    List<Admin> rows = queryFactory.selectFrom(qAdmin)
        .where(predicates)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(qAdmin.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(qAdmin.count())
        .from(qAdmin)
        .where(predicates)
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  private BooleanBuilder buildSearchPredicates(SearchAdminOptions options) {
    BooleanBuilder predicates = new BooleanBuilder();

    if (options.getRole() != null) {
      predicates.and(qAdmin.role.eq(options.getRole()));
    }
    if (options.getDepartmentId() != null) {
      predicates.and(qAdmin.department.id.eq(options.getDepartmentId()));
    }
    if (options.getJobGradeId() != null) {
      predicates.and(qAdmin.jobGrade.id.eq(options.getJobGradeId()));
    }

    if(options.getQuery() != null && !options.getQuery().isEmpty()) {
      String query = options.getQuery().toLowerCase();
      predicates.and(qAdmin.email.containsIgnoreCase(query)
          .or(qAdmin.name.containsIgnoreCase(query))
          .or(qAdmin.phoneNumber.containsIgnoreCase(query)));
    }

    return predicates;
  }
}

