package com.backend.lab.domain.admin.core.repository;

import com.backend.lab.domain.admin.core.entity.DeletedAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedAdminRepository extends JpaRepository<DeletedAdmin, Long>, DeletedAdminRepositoryCustom{

}
