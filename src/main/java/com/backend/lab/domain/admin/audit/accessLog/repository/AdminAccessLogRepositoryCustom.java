package com.backend.lab.domain.admin.audit.accessLog.repository;

import com.backend.lab.domain.admin.audit.accessLog.entity.dto.req.GetAdminAccessLogOptions;
import com.backend.lab.domain.admin.audit.accessLog.entity.AdminAccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminAccessLogRepositoryCustom {

  Page<AdminAccessLog> findLatestLogsEachUser(Pageable pageable);
  Page<AdminAccessLog> search(GetAdminAccessLogOptions options);
}
