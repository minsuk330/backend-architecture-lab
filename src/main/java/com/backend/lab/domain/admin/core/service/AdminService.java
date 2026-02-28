package com.backend.lab.domain.admin.core.service;

import com.backend.lab.common.auth.AuthenticateUserService;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.entity.dto.req.AdminCreateReq;
import com.backend.lab.domain.admin.core.entity.dto.req.AdminUpdateReq;
import com.backend.lab.domain.admin.core.entity.dto.req.SearchAdminOptions;
import com.backend.lab.domain.admin.core.entity.dto.resp.AdminResp;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import com.backend.lab.domain.admin.core.repository.AdminRepository;
import com.backend.lab.domain.uploadFile.entity.dto.resp.UploadFileResp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService implements AuthenticateUserService {

  private final AdminRepository adminRepository;
  private final PasswordEncoder passwordEncoder;

  public List<Admin> gets(List<Long> ids) {
    return adminRepository.findAllById(ids);
  }

  public List<Admin> gets(List<Long> ids, AdminRole role) {
    return adminRepository.findAllByIdAndRole(ids,role);
  }
  public List<Admin> gets() {
    return adminRepository.findAll();
  }
  
  public List<Admin> getAllByRole(AdminRole role) {
    return adminRepository.findAllByRole(role);
  }

  public List<Admin> getsByPermissionId(Long permissionId) {
    return adminRepository.findAllByPermissionId(permissionId);
  }

  public List<Admin> getsByDepartmentId(Long departmentId) {
    return adminRepository.findAllByDepartmentId(departmentId);
  }

  public List<Admin> getsWithJobGrade() {
    return adminRepository.findAllWithJobGrade();
  }

  public List<Admin> getsByJobGradeId(Long jobGradeId) {
    return adminRepository.findAllByJobGradeId(jobGradeId);
  }

  public Page<Admin> search(SearchAdminOptions options) {
    return adminRepository.search(options);
  }

  @Override
  public Admin getById(Long memberId) {
    return adminRepository.findById(memberId)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Admin"));
  }

  public Admin getByIdNullable(Long memberId) {
    if (memberId == null) {
      return null;
    }
    return adminRepository.findById(memberId).orElse(null);
  }

  public Admin getByEmail(String email) {
    return adminRepository.findByEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Admin"));
  }

  public boolean existByEmail(String email) {
    return adminRepository.existsByEmail(email);
  }

  @Transactional
  public Admin create(AdminCreateReq req) {
    if (this.existByEmail(req.getEmail())) {
      throw new BusinessException(ErrorCode.DUPLICATED_ADMIN_EMAIL);
    }

    Admin admin = Admin.builder()
        .email(req.getEmail())
        .password(passwordEncoder.encode(req.getPassword()))
        .name(req.getName())
        .birth(req.getBirth())
        .phoneNumber(req.getPhoneNumber())
        .officePhoneNumber(req.getOfficePhoneNumber())
        .profileImage(req.getProfileImage())
        .gender(req.getGender())
        .role(req.getRole())
        .build();

    return adminRepository.save(admin);
  }

  @Transactional
  public void update(Long id, AdminUpdateReq req) {
    Admin admin = this.getById(id);

    if (!admin.getEmail().equals(req.getEmail()) && this.existByEmail(req.getEmail())) {
      throw new BusinessException(ErrorCode.DUPLICATED_ADMIN_EMAIL);
    }

    admin.setEmail(req.getEmail());
    if (req.getPassword() != null && !req.getPassword().isEmpty()) {
      admin.setPassword(passwordEncoder.encode(req.getPassword()));
    }

    admin.setName(req.getName());
    admin.setBirth(req.getBirth());
    admin.setPhoneNumber(req.getPhoneNumber());
    admin.setOfficePhoneNumber(req.getOfficePhoneNumber());
    admin.setProfileImage(req.getProfileImage());
    admin.setGender(req.getGender());
    admin.setRole(req.getRole());
  }

  @Transactional
  public void delete(Long id) {
    Admin admin = this.getById(id);
    adminRepository.delete(admin);
  }

  public AdminResp adminResp(Admin admin, UploadFileResp profileImage) {
    if (admin == null) {
      return AdminResp.builder()
          .name("삭제된 관리자")
          .build();
    }


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

  public Admin findByName(String adminName) {
    return adminRepository.findByName(adminName);
  }
}
