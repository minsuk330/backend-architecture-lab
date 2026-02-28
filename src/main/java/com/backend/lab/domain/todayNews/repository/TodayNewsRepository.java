package com.backend.lab.domain.todayNews.repository;

import com.backend.lab.domain.todayNews.entity.TodayNews;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodayNewsRepository extends JpaRepository<TodayNews, Long>, TodayNewsRepositoryCustom {

}
