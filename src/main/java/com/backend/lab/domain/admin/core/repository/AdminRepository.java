package com.backend.lab.domain.admin.core.repository;

import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminRepository extends JpaRepository<Admin, Long>, AdminRepositoryCustom {

  Optional<Admin> findByEmail(String email);
  List<Admin> findAllByDepartmentId(Long departmentId);
  List<Admin> findAllByPermissionId(Long permissionId);
  List<Admin> findAllByJobGradeId(Long jobGradeId);
  boolean existsByEmail(String email);

  @Query("select a from Admin a left join fetch a.jobGrade order by a.jobGrade.rank desc ")
  List<Admin> findAllWithJobGrade();
  
  @Query("select a from Admin a left join fetch a.jobGrade where a.role = :role order by a.jobGrade.rank desc")
  List<Admin> findAllByRole(AdminRole role);

  Admin findByName(String adminName);
}
