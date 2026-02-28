package com.backend.lab.domain.todayNews.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.todayNews.entity.TodayNews;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsDTO;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsResp;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsSearchOptions;
import com.backend.lab.domain.todayNews.repository.TodayNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodayNewsService {

  private final TodayNewsRepository todayNewsRepository;

  public TodayNews getById(Long id) {
    return todayNewsRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "오늘의 뉴스"));
  }

  public Page<TodayNews> search(TodayNewsSearchOptions options) {
    return todayNewsRepository.search(options);
  }

  @Transactional
  public TodayNews create(TodayNewsDTO dto, Admin admin) {
    return todayNewsRepository.save(
        TodayNews.builder()
            .createdBy(admin)
            .title(dto.getTitle())
            .company(dto.getCompany())
            .url(dto.getUrl())
            .build()
    );
  }

  @Transactional
  public TodayNews update(Long id, TodayNewsDTO dto) {
    TodayNews todayNews = this.getById(id);

    todayNews.setTitle(dto.getTitle());
    todayNews.setCompany(dto.getCompany());
    todayNews.setUrl(dto.getUrl());

    return todayNewsRepository.save(todayNews);
  }

  @Transactional
  public void delete(Long id) {
    TodayNews todayNews = this.getById(id);
    todayNewsRepository.delete(todayNews);
  }

  public TodayNewsResp todayNewsResp(TodayNews todayNews, AdminResp admin) {
    return TodayNewsResp.builder()
        .id(todayNews.getId())
        .createdAt(todayNews.getCreatedAt())

        .title(todayNews.getTitle())
        .company(todayNews.getCompany())
        .url(todayNews.getUrl())
        .createdBy(admin)
        .build();
  }
}
