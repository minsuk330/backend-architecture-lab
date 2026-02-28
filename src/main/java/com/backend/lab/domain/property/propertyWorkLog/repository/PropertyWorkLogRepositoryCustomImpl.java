package com.backend.lab.domain.property.propertyWorkLog.repository;

import com.backend.lab.domain.admin.core.entity.QAdmin;
import com.backend.lab.domain.property.core.entity.QProperty;
import com.backend.lab.domain.property.core.entity.information.QAddressInformation;
import com.backend.lab.domain.property.propertyWorkLog.entity.QPropertyWorkLog;
import com.backend.lab.domain.property.propertyWorkLog.entity.QPropertyWorkLogDetail;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.PropertyWorkLogDetailOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.PropertyWorkLogDetailsOptions;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp.PropertyWorkLogDetailResp;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.resp.PropertyWorkLogResp;
import com.backend.lab.domain.property.propertyWorkLog.entity.dto.req.SearchPropertyWorkLogOptions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class PropertyWorkLogRepositoryCustomImpl implements PropertyWorkLogRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QPropertyWorkLog qpropertyWorkLog = QPropertyWorkLog.propertyWorkLog;
  private final QProperty qproperty = QProperty.property;
  private final QAdmin qadmin = QAdmin.admin;
  private final QAddressInformation qAddress = QAddressInformation.addressInformation;
  private final QPropertyWorkLogDetail qPropertyWorkLogDetail = QPropertyWorkLogDetail.propertyWorkLogDetail;

  @Override
  public Page<PropertyWorkLogResp> search(SearchPropertyWorkLogOptions options) {
    BooleanBuilder booleanBuilder = buildSearchPredicates(options);

    List<PropertyWorkLogResp> rows = queryFactory
        .select(Projections.constructor(PropertyWorkLogResp.class,
            qpropertyWorkLog.id,
            qpropertyWorkLog.createdAt,
            qpropertyWorkLog.updatedAt,
            qpropertyWorkLog.deletedAt,
            qpropertyWorkLog.workLogType,
            qproperty.buildingName,
            qAddress.properties.jibunAddress,
            qadmin.name,
            qpropertyWorkLog.adminIp,
            qproperty.id
        ))
        .from(qpropertyWorkLog)
        .leftJoin(qpropertyWorkLog.property, qproperty)
        .leftJoin(qpropertyWorkLog.createdBy, qadmin)
        .leftJoin(qproperty.address, qAddress)
        .where(booleanBuilder
            .and(qpropertyWorkLog.property.delete_persistent_at.isNull()))
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(qpropertyWorkLog.createdAt.desc())
        .fetch();

    // Count 쿼리
    Long total = queryFactory
        .select(qpropertyWorkLog.count())
        .from(qpropertyWorkLog)
        .where(booleanBuilder
            .and(qpropertyWorkLog.property.delete_persistent_at.isNull()))
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }


  @Override
  public Page<PropertyWorkLogDetailResp> getByWorkLogId(PropertyWorkLogDetailOptions options) {
    List<PropertyWorkLogDetailResp> rows = queryFactory
        .select(Projections.constructor(PropertyWorkLogDetailResp.class,
            qpropertyWorkLog.id,
            qpropertyWorkLog.createdAt,
            qpropertyWorkLog.updatedAt,
            qpropertyWorkLog.deletedAt,
            qPropertyWorkLogDetail.logFieldType,
            qpropertyWorkLog.workLogType,
            qPropertyWorkLogDetail.beforeValue,
            qPropertyWorkLogDetail.afterValue,
            qadmin.name
        ))
        .from(qPropertyWorkLogDetail)
        .join(qPropertyWorkLogDetail.propertyWorkLog, qpropertyWorkLog)
        .leftJoin(qpropertyWorkLog.createdBy, qadmin)
        .where(qpropertyWorkLog.id.eq(options.getWorkLogId()))
        .orderBy(qPropertyWorkLogDetail.createdAt.desc())
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    // 총 개수 조회
    Long total = queryFactory
        .select(qPropertyWorkLogDetail.count())
        .from(qPropertyWorkLogDetail)
        .join(qPropertyWorkLogDetail.propertyWorkLog, qpropertyWorkLog)
        .where(qpropertyWorkLog.id.eq(options.getWorkLogId()))
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  @Override
  public Page<PropertyWorkLogDetailResp> getsByPropertyId(PropertyWorkLogDetailsOptions options) {
    List<PropertyWorkLogDetailResp> rows = queryFactory
        .select(Projections.constructor(PropertyWorkLogDetailResp.class,
            qpropertyWorkLog.id,
            qpropertyWorkLog.createdAt,
            qpropertyWorkLog.updatedAt,
            qpropertyWorkLog.deletedAt,
            qPropertyWorkLogDetail.logFieldType,
            qpropertyWorkLog.workLogType,
            qPropertyWorkLogDetail.beforeValue,
            qPropertyWorkLogDetail.afterValue,
            qadmin.name
        ))
        .from(qPropertyWorkLogDetail)
        .join(qPropertyWorkLogDetail.propertyWorkLog, qpropertyWorkLog)
        .leftJoin(qpropertyWorkLog.createdBy, qadmin)
        .where(qpropertyWorkLog.property.id.eq(options.getPropertyId()))
        .orderBy(
            qPropertyWorkLogDetail.createdAt.desc()
        )
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .fetch();

    // 총 개수 조회
    Long total = queryFactory
        .select(qPropertyWorkLogDetail.count())
        .from(qPropertyWorkLogDetail)
        .join(qPropertyWorkLogDetail.propertyWorkLog, qpropertyWorkLog)
        .where(qpropertyWorkLog.property.id.eq(options.getPropertyId()))
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }



  private BooleanBuilder buildSearchPredicates(SearchPropertyWorkLogOptions options) {
    BooleanBuilder booleanBuilder = new BooleanBuilder();

    if (options.getQuery() != null && !options.getQuery().isBlank()) {
      BooleanExpression queryExp = qproperty.buildingName.containsIgnoreCase(options.getQuery())
          .or(qAddress.properties.jibunAddress.containsIgnoreCase(options.getQuery()));
      booleanBuilder.and(queryExp);
    }
    if (options.getWorkLogType()!=null) {
      booleanBuilder.and(qpropertyWorkLog.workLogType.eq(options.getWorkLogType()));
    }

    return booleanBuilder;
  }

}
