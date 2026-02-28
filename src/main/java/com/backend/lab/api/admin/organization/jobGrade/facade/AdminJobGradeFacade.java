package com.backend.lab.api.admin.organization.jobGrade.facade;

import com.backend.lab.api.admin.organization.jobGrade.dto.req.JobGradleAssignReq;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.admin.organization.jobGrade.entity.JobGrade;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req.JobGradeCreateReq;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req.JobGradeRerankReq;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req.JobGradeUpdateReq;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.resp.JobGradeResp;
import com.backend.lab.domain.admin.organization.jobGrade.service.JobGradeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminJobGradeFacade {

  private final JobGradeService jobGradeService;
  private final AdminService adminService;

  public ListResp<JobGradeResp> gets() {
    List<JobGradeResp> data = jobGradeService.gets().stream()
        .map(jobGradeService::jobGradeResp)
        .toList();
    return new ListResp<>(data);
  }

  public JobGradeResp getById(Long jobGradeId) {
    JobGrade jobGrade = jobGradeService.getById(jobGradeId);
    return jobGradeService.jobGradeResp(jobGrade);
  }

  @Transactional
  public void create(JobGradeCreateReq req) {
    jobGradeService.create(req);
  }

  @Transactional
  public void assign(JobGradleAssignReq req) {
    Long jobGradeId = req.getJobGradeId();

    Admin admin = adminService.getById(req.getAdminId());

    JobGrade newJobGrade = null;
    if(jobGradeId != null) {
      newJobGrade = jobGradeService.getById(jobGradeId);
    }

    admin.setJobGrade(newJobGrade);
  }

  @Transactional
  public void rerank(JobGradeRerankReq req) {
    jobGradeService.rerank(req);
  }


  @Transactional
  public void update(Long jobGradeId, JobGradeUpdateReq req) {
    jobGradeService.update(jobGradeId, req);
  }

  @Transactional
  public void delete(Long jobGradeId) {
    List<Admin> assignedAdmins = adminService.getsByJobGradeId(jobGradeId);
    JobGrade jobGrade = jobGradeService.getById(jobGradeId);

    JobGrade next = null;
    Long nextId = jobGrade.getMigrationJobGradeId();

    if(nextId != null) {
      next = jobGradeService.getById(nextId);
    }

    for (Admin admin : assignedAdmins) {
      admin.setJobGrade(next);
    }

    jobGradeService.delete(jobGradeId);
  }

}
