package com.backend.lab.domain.admin.permission.repository;

import com.backend.lab.domain.admin.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

}
