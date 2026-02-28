package com.backend.lab.domain.propertyRequest.repository;


import com.backend.lab.api.admin.PropertyRequest.dto.req.SearchPropertyRequestOptions;
import com.backend.lab.domain.propertyRequest.entity.PropertyRequest;
import com.backend.lab.domain.propertyRequest.entity.QPropertyRequest;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class PropertyRequestRepositoryCustomImpl implements PropertyRequestRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QPropertyRequest qPropertyRequest = QPropertyRequest.propertyRequest;


  @Override
  public Page<PropertyRequest> agentSearch(SearchPropertyRequestOptions options) {
    BooleanBuilder builder = search(options);
    builder.and(qPropertyRequest.type.eq(RequestType.AGENT));

    return getPropertyRequests(options, builder);
  }

  private Page<PropertyRequest> getPropertyRequests(SearchPropertyRequestOptions options,
      BooleanBuilder builder) {
    List<PropertyRequest> rows = queryFactory.selectFrom(qPropertyRequest)
        .leftJoin(qPropertyRequest.requester).fetchJoin()
        .where(builder)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(qPropertyRequest.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(qPropertyRequest.count())
        .from(qPropertyRequest)
        .where(builder)
        .fetchOne();
    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  @Override
  public Page<PropertyRequest> sellerSearch(SearchPropertyRequestOptions options) {
    BooleanBuilder builder = search(options);

    builder.and(qPropertyRequest.type.eq(RequestType.SELLER));

    return getPropertyRequests(options, builder);
  }

  @Override
  public Page<PropertyRequest> customerSearch(SearchPropertyRequestOptions options) {
    BooleanBuilder builder = nonMemberSearch(options);

    builder.and(qPropertyRequest.type.eq(RequestType.NON_MEMBER));


    List<PropertyRequest> rows = queryFactory.selectFrom(qPropertyRequest)
        .where(builder)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(qPropertyRequest.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(qPropertyRequest.count())
        .from(qPropertyRequest)
        .where(builder)
        .fetchOne();
    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  public BooleanBuilder nonMemberSearch(SearchPropertyRequestOptions options) {
    BooleanBuilder builder = new BooleanBuilder();

    if (options.getQuery() != null&&!options.getQuery().isEmpty()) {
      BooleanExpression booleanExpression = qPropertyRequest.buildingName.containsIgnoreCase(options.getQuery())
          .or(qPropertyRequest.nonMemberName.containsIgnoreCase(options.getQuery()));
      builder.and(booleanExpression);
    }
    if (options.getStatus()!=null) {
      builder.and(qPropertyRequest.status.eq(options.getStatus()));
    }
    return builder;
  }


  public BooleanBuilder search(SearchPropertyRequestOptions options) {
    BooleanBuilder builder = new BooleanBuilder();

    if (options.getQuery() != null&&!options.getQuery().isEmpty()) {
      BooleanExpression booleanExpression = qPropertyRequest.buildingName.containsIgnoreCase(options.getQuery())
          .or(qPropertyRequest.requester.id.stringValue().eq(options.getQuery()));
      builder.and(booleanExpression);
    }
    if (options.getStatus()!=null) {
      builder.and(qPropertyRequest.status.eq(options.getStatus()));
    }
    return builder;
  }

}
