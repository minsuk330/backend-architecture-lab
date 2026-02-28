package com.backend.lab.api.admin.organization.jobGrade.controller;

import com.backend.lab.api.admin.organization.jobGrade.dto.req.JobGradleAssignReq;
import com.backend.lab.api.admin.organization.jobGrade.facade.AdminJobGradeFacade;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req.JobGradeCreateReq;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req.JobGradeRerankReq;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req.JobGradeUpdateReq;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.resp.JobGradeResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequestMapping("/admin/organization/job-grade")
@RequiredArgsConstructor
@Tag(name = "[관리자] 직급 관리")
public class AdminJobGradeController {

  private final AdminJobGradeFacade adminJobGradeFacade;

  @Operation(summary = "직급 목록 조회")
  @GetMapping
  public ListResp<JobGradeResp> getList() {
    return adminJobGradeFacade.gets();
  }

  @Operation(summary = "직급 상세 조회")
  @GetMapping("/{jobGradeId}")
  public JobGradeResp get(
      @PathVariable("jobGradeId") Long jobGradeId
  ) {
    return adminJobGradeFacade.getById(jobGradeId);
  }

  @Operation(summary = "직급 생성")
  @PostMapping
  public void create(
      @RequestBody JobGradeCreateReq req
  ) {
    adminJobGradeFacade.create(req);
  }

  @Operation(summary = "직급 할당")
  @PostMapping("/assign")
  public void assign(
      @RequestBody JobGradleAssignReq req
  ) {
    adminJobGradeFacade.assign(req);
  }

  @Operation(summary = "직급 우선순위 변경")
  @PostMapping("/rerank")
  public void rerank(
      @RequestBody JobGradeRerankReq req
  ) {
    adminJobGradeFacade.rerank(req);
  }

  @Operation(summary = "직급 수정")
  @PutMapping("/{jobGradeId}")
  public void update(
      @PathVariable("jobGradeId") Long jobGradeId,
      @RequestBody JobGradeUpdateReq req
  ) {
    adminJobGradeFacade.update(jobGradeId, req);
  }

  @Operation(summary = "직급 삭제")
  @DeleteMapping("/{jobGradeId}")
  public void delete(
      @PathVariable("jobGradeId") Long jobGradeId
  ) {
    adminJobGradeFacade.delete(jobGradeId);
  }
}
