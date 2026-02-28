package com.backend.lab.domain.admin.core.entity;

import com.backend.lab.common.auth.AuthenticateUser;
import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.common.entity.vo.GenderType;
import com.backend.lab.domain.admin.core.entity.vo.AdminRole;
import com.backend.lab.domain.admin.organization.department.entity.Department;
import com.backend.lab.domain.admin.organization.jobGrade.entity.JobGrade;
import com.backend.lab.domain.admin.permission.entity.Permission;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "admin")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL AND delete_persistent_at IS NULL")
@SQLDelete(sql = "UPDATE admin SET deleted_at = NOW() WHERE id = ?")
public class Admin extends BaseEntity implements AuthenticateUser {

  @Column(name = "delete_persistent_at", nullable = true)
  private LocalDateTime delete_persistent_at;

  private String email;
  private String password;
  private String name;
  private LocalDate birth;
  private String phoneNumber;
  private String officePhoneNumber;

  @JoinColumn(name = "profile_image_file_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UploadFile profileImage;

  private GenderType gender;

  @Enumerated(EnumType.STRING)
  private AdminRole role;

  @JoinColumn(name = "department_id")
  @ManyToOne(fetch = FetchType.EAGER)
  private Department department;

  @JoinColumn(name = "permission_id")
  @ManyToOne(fetch = FetchType.EAGER)
  private Permission permission;

  @JoinColumn(name = "job_grade_id")
  @ManyToOne(fetch = FetchType.EAGER)
  private JobGrade jobGrade;

  @Override
  public String[] getAuthorities() {
    return new String[]{Arrays.toString(role.getRoleName())};
  }
}
