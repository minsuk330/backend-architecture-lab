package com.backend.lab.domain.admin.organization.department.repository;

import com.backend.lab.domain.admin.organization.department.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
