package com.backend.lab.domain.admin.core.repository;

import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.dto.req.SearchAdminOptions;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import java.util.List;
import org.springframework.data.domain.Page;

public interface AdminRepositoryCustom {

  List<Admin> findAllByIdAndRole(List<Long> ids, AdminRole role);

  Page<Admin> search(SearchAdminOptions options);
}
