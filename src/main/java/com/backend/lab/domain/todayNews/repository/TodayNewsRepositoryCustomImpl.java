package com.backend.lab.domain.todayNews.repository;

import com.backend.lab.domain.todayNews.entity.QTodayNews;
import com.backend.lab.domain.todayNews.entity.TodayNews;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsSearchOptions;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class TodayNewsRepositoryCustomImpl implements TodayNewsRepositoryCustom{

  private final JPAQueryFactory queryFactory;
  private final QTodayNews qTodayNews = QTodayNews.todayNews;

  @Override
  public Page<TodayNews> search(TodayNewsSearchOptions options) {

    BooleanBuilder builder = new BooleanBuilder();
    if(options.getQuery()!=null && !options.getQuery().isEmpty()) {
      builder.and(
          qTodayNews.title.containsIgnoreCase(options.getQuery())
              .or(qTodayNews.createdBy.name.containsIgnoreCase(options.getQuery()))
      );
    }

    List<TodayNews> rows = queryFactory.selectFrom(qTodayNews)
        .where(builder)
        .offset(options.pageable().getOffset())
        .limit(options.pageable().getPageSize())
        .orderBy(qTodayNews.createdAt.desc())
        .fetch();

    Long total = queryFactory
        .select(qTodayNews.count())
        .from(qTodayNews)
        .where(builder)
        .fetchOne();

    return new PageImpl<>(rows, options.pageable(), total != null ? total : 0);
  }
}
