package com.backend.lab.domain.post.repository;

import com.backend.lab.domain.post.entity.Post;
import com.backend.lab.domain.post.entity.QPost;
import com.backend.lab.domain.post.entity.dto.req.SearchPostOptions;
import com.backend.lab.domain.post.entity.vo.PostViewType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

  private final JPAQueryFactory queryFactory;
  private final QPost post = QPost.post;

  @Override
  public Page<Post> search(SearchPostOptions options, PostViewType viewType) {
    BooleanBuilder predicates = buildSearchPredicates(options, viewType);

    var rows = queryFactory.selectFrom(post)
        .where(predicates)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(post.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(post.count())
        .from(post)
        .where(predicates)
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }

  public BooleanBuilder buildSearchPredicates(SearchPostOptions options, PostViewType viewType) {
    BooleanBuilder predicates = new BooleanBuilder();

    if (options.getQuery() != null && !options.getQuery().isBlank()) {
      predicates.and(post.title.containsIgnoreCase(options.getQuery())
          .or(post.createdBy.name.containsIgnoreCase(options.getQuery())));
    }

    if (options.getType() != null) {
      predicates.and(post.type.eq(options.getType()));
    }

    switch (viewType) {
      case AGENT:
        predicates.and(post.canAgentView.eq(true));
        break;
      case BUYER:
        predicates.and(post.canBuyerView.eq(true));
        break;
      case SELLER:
        predicates.and(post.canSellerView.eq(true));
        break;
      case ALL:
      default:
        break;
    }

    return predicates;
  }
}
