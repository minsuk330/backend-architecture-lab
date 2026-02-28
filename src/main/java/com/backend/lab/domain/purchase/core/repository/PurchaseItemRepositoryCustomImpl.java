package com.backend.lab.domain.purchase.core.repository;

import com.backend.lab.domain.member.core.entity.QMember;
import com.backend.lab.domain.purchase.core.entity.PurchaseItem;
import com.backend.lab.domain.purchase.core.entity.QPurchase;
import com.backend.lab.domain.purchase.core.entity.QPurchaseItem;
import com.backend.lab.domain.purchase.core.entity.dto.SearchPurchaseOptions;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PurchaseItemRepositoryCustomImpl implements PurchaseItemRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QPurchaseItem purchaseItem = QPurchaseItem.purchaseItem;
  private final QPurchase purchase = QPurchase.purchase;
  private final QMember member = QMember.member;

  @Override
  public Page<PurchaseItem> search(SearchPurchaseOptions options) {
    JPAQuery<PurchaseItem> query = queryFactory
        .selectFrom(purchaseItem)
        .join(purchaseItem.purchase, purchase)
        .join(member).on(purchase.memberId.eq(member.id));

    BooleanBuilder where = new BooleanBuilder();

    where.and(
        purchase.status.eq(PurchaseStatus.SUCCESS)
    );

    if (options.getProductId() != null) {
      where.and(purchaseItem.product.id.eq(options.getProductId()));
    }

    Pageable pageable = options.pageable();

    // 페이징 처리
    List<PurchaseItem> content = query
        .where(where)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(purchaseItem.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(purchaseItem.count())
        .from(purchaseItem)
        .join(purchaseItem.purchase, purchase)
        .join(member).on(purchase.memberId.eq(member.id))
        .where(query.getMetadata().getWhere())
        .fetchOne();

    return new PageImpl<>(content, pageable, total != null ? total : 0);
  }
}
