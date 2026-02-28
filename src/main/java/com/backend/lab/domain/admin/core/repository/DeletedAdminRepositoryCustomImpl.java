package com.backend.lab.domain.admin.core.repository;

import com.backend.lab.domain.admin.core.entity.DeletedAdmin;
import com.backend.lab.domain.admin.core.entity.QDeletedAdmin;
import com.backend.lab.domain.admin.core.entity.dto.req.SearchAdminOptions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class DeletedAdminRepositoryCustomImpl implements DeletedAdminRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QDeletedAdmin qDeletedAdmin = QDeletedAdmin.deletedAdmin;

  @Override
  public Page<DeletedAdmin> search(SearchAdminOptions options) {
    BooleanBuilder predicates = buildSearchPredicates(options);

    List<DeletedAdmin> rows = queryFactory.selectFrom(qDeletedAdmin)
        .where(predicates)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(qDeletedAdmin.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(qDeletedAdmin.count())
        .from(qDeletedAdmin)
        .where(predicates)
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  private BooleanBuilder buildSearchPredicates(SearchAdminOptions options) {
    BooleanBuilder predicates = new BooleanBuilder();

    if (options.getRole() != null) {
      predicates.and(qDeletedAdmin.role.eq(options.getRole()));
    }
    if (options.getDepartmentId() != null) {
      predicates.and(qDeletedAdmin.department.id.eq(options.getDepartmentId()));
    }
    if (options.getJobGradeId() != null) {
      predicates.and(qDeletedAdmin.jobGrade.id.eq(options.getJobGradeId()));
    }

    if (options.getQuery() != null && !options.getQuery().isEmpty()) {
      String query = options.getQuery().toLowerCase();
      predicates.and(qDeletedAdmin.email.containsIgnoreCase(query)
          .or(qDeletedAdmin.name.containsIgnoreCase(query))
          .or(qDeletedAdmin.phoneNumber.containsIgnoreCase(query)));
    }

    return predicates;
  }
}
