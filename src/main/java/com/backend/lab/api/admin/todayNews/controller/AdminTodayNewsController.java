package com.backend.lab.api.admin.todayNews.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.todayNews.facade.AdminTodayNewsFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsDTO;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsResp;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsSearchOptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/today-news")
@RequiredArgsConstructor
@Tag(name = "[관리자] 오늘의 뉴스")
public class AdminTodayNewsController {

  private final AdminTodayNewsFacade adminTodayNewsFacade;

  @Operation(summary = "오늘의 뉴스 검색")
  @GetMapping
  public PageResp<TodayNewsResp> search(
      @ModelAttribute TodayNewsSearchOptions options
  ) {
    return adminTodayNewsFacade.search(options);
  }

  @Operation(summary = "오늘의 뉴스 상세 조회")
  @GetMapping("/{id}")
  public TodayNewsResp getById(
      @PathVariable("id") Long id
  ) {
    return adminTodayNewsFacade.getById(id);
  }

  @PreAuthorize("hasAuthority('MANAGE_TODAY_NEWS')")
  @Operation(summary = "오늘의 뉴스 생성")
  @PostMapping
  public void create(
      @RequestBody TodayNewsDTO req
  ) {
    adminTodayNewsFacade.create(getUserId(), req);
  }

  @PreAuthorize("hasAuthority('MANAGE_TODAY_NEWS')")
  @Operation(summary = "오늘의 뉴스 수정")
  @PutMapping("/{id}")
  public void update(
      @PathVariable("id") Long id,
      @RequestBody TodayNewsDTO req
  ) {
    adminTodayNewsFacade.update(id, req);
  }

  @PreAuthorize("hasAuthority('MANAGE_TODAY_NEWS')")
  @Operation(summary = "오늘의 뉴스 삭제")
  @DeleteMapping("/{id}")
  public void delete(
      @PathVariable("id") Long id
  ) {
    adminTodayNewsFacade.delete(id);
  }
}
