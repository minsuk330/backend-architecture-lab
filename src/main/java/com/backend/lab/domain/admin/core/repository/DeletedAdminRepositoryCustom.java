package com.backend.lab.domain.admin.core.repository;

import com.backend.lab.domain.admin.core.entity.DeletedAdmin;
import com.backend.lab.domain.admin.core.entity.dto.req.SearchAdminOptions;
import org.springframework.data.domain.Page;

public interface DeletedAdminRepositoryCustom {
  Page<DeletedAdmin> search(SearchAdminOptions options);
}
