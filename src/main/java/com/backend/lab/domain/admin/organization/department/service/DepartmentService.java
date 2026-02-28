package com.backend.lab.domain.admin.organization.department.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.admin.organization.department.entity.Department;
import com.backend.lab.domain.admin.organization.department.entity.dto.req.DepartmentCreateReq;
import com.backend.lab.domain.admin.organization.department.entity.dto.req.DepartmentRerankReq;
import com.backend.lab.domain.admin.organization.department.entity.dto.req.DepartmentUpdateReq;
import com.backend.lab.domain.admin.organization.department.entity.dto.resp.DepartmentResp;
import com.backend.lab.domain.admin.organization.department.repository.DepartmentRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {

  private final DepartmentRepository departmentRepository;

  public List<Department> gets() {
    return departmentRepository.findAll(Sort.by("rank").ascending());
  }

  public List<Department> gets(List<Long> ids) {
    return departmentRepository.findAllById(ids);
  }

  public Department getById(Long departmentId) {
    return departmentRepository.findById(departmentId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Department"));
  }

  @Transactional
  public void create(DepartmentCreateReq req) {
    departmentRepository.save(
        Department.builder()
            .rank(req.getRank())
            .name(req.getName())
            .isActive(req.getIsActive())
            .migrationDepartmentId(req.getMigrationDepartmentId())
            .build()
    );
  }

  @Transactional
  public void update(Long departmentId, DepartmentUpdateReq req) {
    Department department = this.getById(departmentId);

    department.setRank(req.getRank());
    department.setName(req.getName());
    department.setIsActive(req.getIsActive());
    department.setMigrationDepartmentId(req.getMigrationDepartmentId());
  }

  @Transactional
  public void rerank(DepartmentRerankReq req) {
    List<Long> orderedIds = req.getIds();
    List<Department> departments = this.gets(orderedIds);
    for (int i = 0; i < orderedIds.size(); i++) {
      Long id = orderedIds.get(i);
      Department department = departments.stream()
          .filter(d -> d.getId().equals(id))
          .findFirst()
          .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Department"));

      department.setRank(i * 10);
    }
  }

  @Transactional
  public void delete(Long departmentId) {
    Department department = this.getById(departmentId);
    this.departmentRepository.delete(department);
  }

  public DepartmentResp departmentResp(Department department) {

    if (department == null) {
      return null;
    }

    DepartmentResp departmentResp = DepartmentResp.builder()
        .id(department.getId())
        .rank(department.getRank())
        .name(department.getName())
        .isActive(department.getIsActive())
        .migrationDepartmentId(department.getMigrationDepartmentId())
        .build();

    Long migrationDepartmentId = department.getMigrationDepartmentId();
    if (migrationDepartmentId != null) {
      Department next = this.getById(migrationDepartmentId);
      departmentResp.setMigrationDepartmentName(next.getName());
    }

    return departmentResp;
  }
}
