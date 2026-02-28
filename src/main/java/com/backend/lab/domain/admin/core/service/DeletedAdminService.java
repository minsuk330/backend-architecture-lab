package com.backend.lab.domain.admin.core.service;

import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.admin.core.entity.DeletedAdmin;
import com.backend.lab.domain.admin.core.entity.dto.req.SearchAdminOptions;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.admin.core.repository.DeletedAdminRepository;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeletedAdminService {

  private final DeletedAdminRepository deletedAdminRepository;

  public DeletedAdmin getById(Long id) {
    return deletedAdminRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Admin"));
  }

  public Page<DeletedAdmin> search(SearchAdminOptions options) {
    return deletedAdminRepository.search(options);
  }

  @Transactional
  public void restore(Long id) {
    DeletedAdmin deletedAdmin = this.getById(id);
    deletedAdmin.setDeletedAt(null);
  }

  @Transactional
  public void remove(Long id) {
    DeletedAdmin deletedAdmin = this.getById(id);
    deletedAdminRepository.delete(deletedAdmin);
  }

  public AdminResp adminResp(DeletedAdmin admin, UploadFileResp profileImage) {
    return AdminResp.builder()
        .id(admin.getId())
        .createdAt(admin.getCreatedAt())
        .updatedAt(admin.getUpdatedAt())
        .deletedAt(admin.getDeletedAt())

        .email(admin.getEmail())
        .name(admin.getName())
        .birth(admin.getBirth())
        .phoneNumber(admin.getPhoneNumber())
        .officePhoneNumber(admin.getOfficePhoneNumber())
        .profileImage(profileImage)
        .gender(admin.getGender())
        .role(admin.getRole())
        .build();
  }
}
