package com.backend.lab.api.member.common.todayNews.facade;

import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.todayNews.entity.TodayNews;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsResp;
import com.backend.lab.domain.todayNews.entity.dto.TodayNewsSearchOptions;
import com.backend.lab.domain.todayNews.service.TodayNewsService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberTodayNewsFacade {

  private final TodayNewsService todayNewsService;
  private final AdminService adminService;
  private final UploadFileService uploadFileService;

  public PageResp<TodayNewsResp> search(TodayNewsSearchOptions options) {
    Page<TodayNews> page = todayNewsService.search(options);
    List<TodayNewsResp> data = page.getContent().stream()
        .sorted(Comparator.nullsLast(Comparator.comparing(TodayNews::getCreatedAt).reversed()))
        .map(this::todayNewsResp)
        .toList();
    return new PageResp<>(page, data);
  }

  public TodayNewsResp getById(Long id) {
    TodayNews todayNews = todayNewsService.getById(id);
    return this.todayNewsResp(todayNews);
  }

  public TodayNewsResp todayNewsResp(TodayNews todayNews) {
    try {
      Admin admin = todayNews.getCreatedBy();
      AdminResp adminResp = adminService.adminResp(admin,
          uploadFileService.uploadFileResp(admin != null ? admin.getProfileImage() : null));
      return todayNewsService.todayNewsResp(todayNews, adminResp);
    } catch (Exception e) {
      return todayNewsService.todayNewsResp(todayNews, adminService.adminResp(null, null));
    }
  }
}
