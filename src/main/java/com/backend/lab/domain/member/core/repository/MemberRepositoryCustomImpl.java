package com.backend.lab.domain.member.core.repository;

import com.backend.lab.api.admin.itemHistory.contactItem.dto.req.AdminContactCountSearchOptions;
import com.backend.lab.api.admin.itemHistory.contactItem.dto.resp.AdminContactCountResp;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAgentListOptions;
import com.backend.lab.api.admin.member.agent.facade.dto.req.AdminAgentSearchOptions;
import com.backend.lab.api.admin.member.buyer.facade.dto.req.AdminBuyerSearchOptions;
import com.backend.lab.api.admin.member.seller.facade.dto.req.AdminSellerSearchOptions;
import com.backend.lab.domain.agentItem.core.entity.QAgentItem;
import com.backend.lab.domain.agentItem.core.entity.QAgentTicket;
import com.backend.lab.domain.agentItem.core.entity.vo.AgentTicketStatus;
import com.backend.lab.domain.agentItem.history.contactItem.entity.QContactItemHistory;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.member.core.entity.QMember;
import com.backend.lab.domain.member.core.entity.dto.req.AdminCustomerSearchOptions;
import com.backend.lab.domain.member.core.entity.dto.req.SearchAgentOptions;
import com.backend.lab.domain.member.core.entity.dto.req.SearchSellerAndCustomerOptions;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final EntityManager entityManager;
  private final QMember qMember = QMember.member;
  private final QAgentItem agentItem = QAgentItem.agentItem;
  private final QAgentTicket ticket = QAgentTicket.agentTicket;
  private final QContactItemHistory contactItemHistory = QContactItemHistory.contactItemHistory;


  @Override
  public Page<Member> searchSellerAndCustomer(SearchSellerAndCustomerOptions options) {
    BooleanBuilder builder = buildSearchSeller(options);

    List<Member> rows = queryFactory.selectFrom(qMember)
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

  @Override
  public Page<Member> searchAgent(SearchAgentOptions options) {
    BooleanBuilder builder = buildSearchAgent(options);

    List<Member> rows = queryFactory.selectFrom(qMember)
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

  @Override
  public Page<Member> searchCustomer(AdminCustomerSearchOptions options) {
    BooleanBuilder builder = new BooleanBuilder();

    builder.and(qMember.type.eq(MemberType.CUSTOMER));

    if (options.getAdminId() != null) {
      builder.and(qMember.customerProperties.adminId.eq(options.getAdminId()));
    }

    if (options.getCompanyType() != null) {
      builder.and(qMember.customerProperties.companyType.eq(options.getCompanyType()));
    }

    if (!StringUtils.isEmpty(options.getQuery())) {
      String query = options.getQuery();
      builder.and(
          qMember.customerProperties.name.containsIgnoreCase(query)
              .or(qMember.customerProperties.address.containsIgnoreCase(query))
              .or(qMember.customerProperties.phoneNumber.containsIgnoreCase(query))
      );
    }

    List<Member> rows = queryFactory.selectFrom(qMember)
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

  @Override
  public Page<Member> adminSearchAgent(AdminAgentSearchOptions options) {
    return withDeletedFilter(false, () -> {

      BooleanBuilder builder = new BooleanBuilder();

      builder.and(qMember.type.eq(MemberType.AGENT));

      if (options.getQuery() != null && !options.getQuery().isEmpty()) {
        String query = options.getQuery();
        builder.and(
            qMember.email.containsIgnoreCase(query)
                .or(qMember.agentProperties.name.contains(query))
                .or(qMember.agentProperties.phoneNumber.contains(query))
        );
      }

      AgentTicketStatus status = options.getStatus();
      if (status != null) {
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
          case REGISTER:
            builder.and(qMember.deletedAt.isNull());
            builder.and(agentItem.isNull().or(agentItem.tickets.isEmpty()));
            break;
          case EXPIRED:
            builder.and(qMember.deletedAt.isNull());
            builder.and(agentItem.isNotNull())
                .and(agentItem.tickets.isNotEmpty())
                .and(JPAExpressions.selectFrom(ticket)
                    .where(ticket.agentItem.eq(agentItem)
                        .and(ticket.ticketStartAt.loe(now))
                        .and(ticket.ticketEndAt.goe(now)))
                    .notExists());
            break;
          case USING:
            builder.and(qMember.deletedAt.isNull());
            builder.and(agentItem.isNotNull())
                .and(JPAExpressions.selectFrom(ticket)
                    .where(ticket.agentItem.eq(agentItem)
                        .and(ticket.ticketStartAt.loe(now))
                        .and(ticket.ticketEndAt.goe(now)))
                    .exists());
            break;
          case DELETED:
            builder.and(qMember.deletedAt.isNotNull());
            break;
        }
      }

      List<Member> rows = queryFactory.selectFrom(qMember)
          .leftJoin(agentItem).on(agentItem.agent.eq(qMember))
          .where(builder)
          .offset(options.pageable().getOffset())
          .limit(options.pageable().getPageSize())
          .orderBy(qMember.createdAt.desc())
          .fetch();

      Long total = queryFactory
          .select(qMember.count())
          .from(qMember)
          .leftJoin(agentItem).on(agentItem.agent.eq(qMember))
          .where(builder)
          .fetchOne();

      return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
    });
  }

  @Override
  public Page<Member> adminSearchBuyer(AdminBuyerSearchOptions options) {
    return withDeletedFilter(false, () -> {
      BooleanBuilder builder = new BooleanBuilder();

      builder.and(qMember.type.eq(MemberType.BUYER));

      String query = options.getQuery();
      if (query != null && !query.isEmpty()) {
        builder.and(qMember.buyerProperties.name.containsIgnoreCase(query)
            .or(qMember.buyerProperties.phoneNumber.containsIgnoreCase(query)));
      }

      Boolean isActive = options.getIsActive();
      if (isActive != null) {
        if (isActive) {
          builder.and(qMember.deletedAt.isNull());
        } else {
          builder.and(qMember.deletedAt.isNotNull());
        }
      }

      List<Member> rows = queryFactory.selectFrom(qMember)
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

    });
  }

  @Override
  public Page<Member> adminSearchSeller(AdminSellerSearchOptions options) {
    return withDeletedFilter(false, () -> {
      BooleanBuilder builder = new BooleanBuilder();

      builder.and(qMember.type.eq(MemberType.SELLER));

      String query = options.getQuery();
      if (query != null && !query.isEmpty()) {
        builder.and(qMember.buyerProperties.name.containsIgnoreCase(query)
            .or(qMember.buyerProperties.phoneNumber.containsIgnoreCase(query)));
      }

      Boolean isActive = options.getIsActive();
      if (isActive != null) {
        if (isActive) {
          builder.and(qMember.deletedAt.isNull());
        } else {
          builder.and(qMember.deletedAt.isNotNull());
        }
      }

      List<Member> rows = queryFactory.selectFrom(qMember)
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

    });
  }

  @Override
  public Page<AdminContactCountResp> searchAgentWithCount(AdminContactCountSearchOptions options) {

    BooleanBuilder where = new BooleanBuilder();

    where.and(qMember.type.eq(MemberType.AGENT));

    String query = options.getQuery();
    if (!StringUtils.isEmpty(query)) {
      where.and(
          qMember.agentProperties.businessName.containsIgnoreCase(query)
              .or(qMember.id.stringValue().containsIgnoreCase(query))
      );
    }

    LocalDate day = options.getDay();
    LocalDateTime start = null;
    LocalDateTime end = null;
    if (day != null) {
      start = day.atStartOfDay();
      end = start.plusDays(1);
    }

    Pageable pageable = options.pageable();

    List<AdminContactCountResp> rows = queryFactory
        .select(Projections.fields(
            AdminContactCountResp.class,
            qMember.id.as("id"),
            qMember.agentProperties.businessName.as("businessName"),
            contactItemHistory.id.count().as("count")
        ))
        .from(qMember)
        .leftJoin(contactItemHistory).on(
            contactItemHistory.agent.id.eq(qMember.id)
                .and(day != null
                    ? contactItemHistory.createdAt.between(start, end)
                    : Expressions.TRUE.isTrue()
                )
        )
        .where(where)
        .groupBy(qMember.id, qMember.agentProperties.businessName)
        .having(contactItemHistory.id.count().goe(1))
        .orderBy(qMember.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(qMember.id.countDistinct())
        .from(qMember)
        .join(contactItemHistory).on(
            contactItemHistory.agent.id.eq(qMember.id)
                .and(day != null
                    ? contactItemHistory.createdAt.between(start, end)
                    : Expressions.TRUE.isTrue())
        )
        .where(where)
        .fetchOne();

    return new PageImpl<>(rows, pageable, total != null ? total : 0);
  }

  public BooleanBuilder buildSearchAgent(SearchAgentOptions options) {
    BooleanBuilder builder = new BooleanBuilder();

    builder.and(qMember.type.eq(MemberType.AGENT));

    if (options.getQuery() != null && !options.getQuery().isBlank()) {
      BooleanExpression queryCondition = qMember.agentProperties.phoneNumber.contains(
              options.getQuery())
          .or(qMember.agentProperties.name.containsIgnoreCase(options.getQuery()))
          .or(qMember.agentProperties.businessName.containsIgnoreCase(options.getQuery()));

      builder.and(queryCondition);
    }
    return builder;
  }


  public BooleanBuilder buildSearchSeller(SearchSellerAndCustomerOptions options) {
    BooleanBuilder builder = new BooleanBuilder();

    builder.and(qMember.type.eq(MemberType.SELLER).or(qMember.type.eq(MemberType.CUSTOMER)));

    if (options.getQuery() != null && !options.getQuery().isBlank()) {
      BooleanExpression queryCondition = qMember.sellerProperties.phoneNumber.contains(
              options.getQuery())
          .or(qMember.sellerProperties.name.contains(options.getQuery()))
          .or(qMember.customerProperties.phoneNumber.contains(options.getQuery()))
          .or(qMember.customerProperties.name.contains(options.getQuery()));

      builder.and(queryCondition);
    }
    if (options.getMemberType() != null) {
      builder.and(qMember.type.eq(options.getMemberType()));
    }

    if (options.getAdminId() != null) {
      builder.and(qMember.sellerProperties.adminId.eq(options.getAdminId()));
    }

    return builder;
  }

  @Override
  public Page<Member> listActiveAgents(
      AdminAgentListOptions options) {
    BooleanBuilder builder = new BooleanBuilder();

    builder.and(qMember.type.eq(MemberType.AGENT));
    builder.and(qMember.deletedAt.isNull());

    String query = options.getQuery();
    if (query != null && !query.isEmpty()) {
      builder.and(
          qMember.email.containsIgnoreCase(query)
              .or(qMember.agentProperties.name.containsIgnoreCase(query))
              .or(qMember.agentProperties.businessName.containsIgnoreCase(query))
              .or(qMember.agentProperties.phoneNumber.contains(query))
      );
    }

    Pageable pageable = options.pageable();

    List<Member> rows = queryFactory.selectFrom(qMember)
        .where(builder)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(qMember.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(qMember.count())
        .from(qMember)
        .where(builder)
        .fetchOne();

    return new PageImpl<>(rows, pageable, total != null ? total : 0);
  }

  private <T> T withDeletedFilter(boolean enableFilter, Supplier<T> supplier) {
    Session session = entityManager.unwrap(Session.class);

    if (!enableFilter) {
      session.disableFilter("deletedMemberFilter");
    }

    try {
      return supplier.get();
    } finally {
      if (!enableFilter) {
        session.enableFilter("deletedMemberFilter");
      }
    }
  }
}
