package com.backend.lab.api.member.common.todayNews.controller;

import com.backend.lab.api.member.common.todayNews.facade.MemberTodayNewsFacade;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsResp;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsSearchOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member/today-news")
@RequiredArgsConstructor
@Tag(name = "[회원/공용] 오늘의 뉴스")
public class MemberTodayNewsController {

  private final MemberTodayNewsFacade memberTodayNewsFacade;

  @Operation(summary = "오늘의 뉴스 검색")
  @GetMapping
  public PageResp<TodayNewsResp> search(
      @ModelAttribute TodayNewsSearchOptions options
  ) {
    return memberTodayNewsFacade.search(options);
  }

  @Operation(summary = "오늘의 뉴스 상세 조회")
  @GetMapping("/{id}")
  public TodayNewsResp getById(
      @PathVariable("id") Long id
  ) {
    return memberTodayNewsFacade.getById(id);
  }
}
