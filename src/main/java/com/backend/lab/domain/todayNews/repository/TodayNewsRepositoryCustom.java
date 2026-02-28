package com.backend.lab.domain.todayNews.repository;

import com.backend.lab.domain.todayNews.entity.TodayNews;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsSearchOptions;
import org.springframework.data.domain.Page;

public interface TodayNewsRepositoryCustom {

  Page<TodayNews> search(TodayNewsSearchOptions options);
}
