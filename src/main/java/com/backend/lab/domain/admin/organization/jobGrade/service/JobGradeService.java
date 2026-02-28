package com.backend.lab.domain.admin.organization.jobGrade.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.admin.organization.jobGrade.entity.JobGrade;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req.JobGradeCreateReq;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req.JobGradeRerankReq;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.req.JobGradeUpdateReq;
import com.backend.lab.domain.admin.organization.jobGrade.entity.dto.resp.JobGradeResp;
import com.backend.lab.domain.admin.organization.jobGrade.repository.JobGradeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobGradeService {

  private final JobGradeRepository jobGradeRepository;

  public Page<JobGrade> gets(Pageable pageable) {
    return jobGradeRepository.findAll(pageable);
  }

  public List<JobGrade> gets() {
    return jobGradeRepository.findAll(Sort.by("rank").ascending());
  }

  public List<JobGrade> gets(List<Long> ids) {
    return jobGradeRepository.findAllById(ids);
  }

  public JobGrade getById(Long jobGradeId) {
    return jobGradeRepository.findById(jobGradeId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "JobGrade"));
  }

  @Transactional
  public void create(JobGradeCreateReq req) {
    jobGradeRepository.save(
        JobGrade.builder()
            .rank(req.getRank())
            .name(req.getName())
            .isActive(req.getIsActive())
            .migrationJobGradeId(req.getMigrationJobGradeId())
            .build()
    );
  }

  @Transactional
  public void update(Long jobGradeId, JobGradeUpdateReq req) {
    JobGrade jobGrade = this.getById(jobGradeId);

    jobGrade.setRank(req.getRank());
    jobGrade.setName(req.getName());
    jobGrade.setIsActive(req.getIsActive());
    jobGrade.setMigrationJobGradeId(req.getMigrationJobGradeId());
  }

  @Transactional
  public void rerank(JobGradeRerankReq req) {

    List<Long> orderedIds = req.getIds();
    List<JobGrade> jobGrades = this.gets(orderedIds);

    for (int i = 0; i < orderedIds.size(); i++) {
      Long id = orderedIds.get(i);
      JobGrade jobGrade = jobGrades.stream()
          .filter(jg -> jg.getId().equals(id))
          .findFirst()
          .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "JobGrade"));

      jobGrade.setRank(i * 10);
    }

  }

  @Transactional
  public void delete(Long jobGradeId) {
    JobGrade jobGrade = this.getById(jobGradeId);
    this.jobGradeRepository.delete(jobGrade);
  }

  public JobGradeResp jobGradeResp(JobGrade jobGrade) {

    if (jobGrade == null) {
      return null;
    }

    JobGradeResp jobGradeResp = JobGradeResp.builder()
        .id(jobGrade.getId())
        .rank(jobGrade.getRank())
        .name(jobGrade.getName())
        .isActive(jobGrade.getIsActive())
        .migrationJobGradeId(jobGrade.getMigrationJobGradeId())
        .build();

    Long migrationJobGradeId = jobGrade.getMigrationJobGradeId();
    if (migrationJobGradeId != null) {
      JobGrade next = this.getById(migrationJobGradeId);
      jobGradeResp.setMigrationJobGradeName(next.getName());
    }

    return jobGradeResp;
  }
}
