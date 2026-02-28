package com.backend.lab.domain.admin.audit.accessLog.repository;

import com.backend.lab.domain.admin.audit.accessLog.entity.AdminAccessLog;
import com.backend.lab.domain.admin.audit.accessLog.entity.vo.AdminAccessLogType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminAccessLogRepository extends JpaRepository<AdminAccessLog, Long>, AdminAccessLogRepositoryCustom {

  AdminAccessLog findTopByAdminIdAndTypeOrderByCreatedAtDesc(Long adminId, AdminAccessLogType type);
  @Query("SELECT a FROM AdminAccessLog a WHERE a.adminId = :adminId ORDER BY a.createdAt DESC")
  Page<AdminAccessLog> findAllByAdminId(Long adminId, Pageable pageable);
}
