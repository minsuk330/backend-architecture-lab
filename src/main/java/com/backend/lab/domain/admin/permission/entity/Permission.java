package com.backend.lab.domain.admin.permission.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.admin.permission.entity.vo.PermissionAuthority;
import com.backend.lab.domain.admin.permission.entity.vo.SecretMemoExposeLevel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "permission")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class Permission extends BaseEntity {

  private Integer rank;

  private String name; // 권한명
  private Boolean active; // 권한 활성화 여부
  private Long migrationPermissionId; // 권한 삭제 시, 마이그레이션을 위한 ID

  private Boolean maemoolActive; // 매물 관련 권한 활성화 여부
  private Boolean canManageMaemool; // 매물 관리
  @Enumerated(EnumType.STRING)
  private SecretMemoExposeLevel secretMemoExposeLevel; // 비밀 메모 노출 레벨

  private Boolean interestActive; // 관심 매물 관련 권한 활성화 여부
  private Boolean canManageInterestGroup; // 관심 그룹 관리

  private Boolean requestRegisterActive; // 매물 등록 요청 관련 권한
  private Boolean canManageRegisterState; // 상태 관리

  private Boolean customerActive; // 고객 관련 권한
  private Boolean canManageCustomer; // 고객 관리

  private Boolean boardActive; // 게시판 관련 권한
  private Boolean canManageNotice; // 공지사항 관리
  private Boolean canManageEvent; // 이벤트 관리
  private Boolean canManagePopup; // 팝업 관리
  private Boolean canManageTodayNews; // 오늘의 뉴스 관리

  private Boolean homePageActive; // 홈페이지 관련 권한
  private Boolean canManageCategory; // 카테고리 관리
  private Boolean canManagePrint; // 프린트 설정
  private Boolean canManageMarketingThumbnail; // 마케팅 썸네일 설정
  private Boolean canManageMarketingSlogan; // 홍보문구 설정

  private Boolean taskAndCustomerHistoryActive; // 작업/고객 이력 관련 권한

  private Boolean purchaseHistoryActive; // 결제 내역 관련 권한

  private Boolean usageHistoryActive; // 사용 내역 관련 권한

  public List<String> getAuthorities() {

    if (active == null || !active) {
      return List.of();
    }

    Set<PermissionAuthority> authorities = new HashSet<>();
    if (maemoolActive) {
      if (canManageMaemool) {
        authorities.add(PermissionAuthority.MANAGE_MAEMOOL);
      }
      if (secretMemoExposeLevel != null) {
        switch (secretMemoExposeLevel) {
          case ME -> authorities.add(PermissionAuthority.SECRET_MEMO_EXPOSE_ME);
          case DEPARTMENT -> authorities.add(PermissionAuthority.SECRET_MEMO_EXPOSE_DEPARTMENT);
          case ALL -> authorities.add(PermissionAuthority.SECRET_MEMO_EXPOSE_ALL);
        }
      }
    }

    if (interestActive) {
      if (canManageInterestGroup) {
        authorities.add(PermissionAuthority.MANAGE_INTEREST_GROUP);
      }
    }

    if (requestRegisterActive) {
      if (canManageRegisterState) {
        authorities.add(PermissionAuthority.MANAGER_REGISTER_STATE);
      }
    }

    if (customerActive) {
      if (canManageCustomer) {
        authorities.add(PermissionAuthority.MANAGE_CUSTOMER);
      }
    }

    if (boardActive) {
      if (canManageNotice) {
        authorities.add(PermissionAuthority.MANAGE_NOTICE);
      }
      if (canManageEvent) {
        authorities.add(PermissionAuthority.MANAGE_EVENT);
      }
      if (canManagePopup) {
        authorities.add(PermissionAuthority.MANAGE_POPUP);
      }
      if (canManageTodayNews) {
        authorities.add(PermissionAuthority.MANAGE_TODAY_NEWS);
      }
    }

    if (homePageActive) {
      if (canManageCategory) {
        authorities.add(PermissionAuthority.MANAGE_CATEGORY);
      }
      if (canManagePrint) {
        authorities.add(PermissionAuthority.MANAGE_PRINT);
      }
      if (canManageMarketingThumbnail) {
        authorities.add(PermissionAuthority.MANAGE_MARKETING_THUMBNAIL);
      }
      if (canManageMarketingSlogan) {
        authorities.add(PermissionAuthority.MANAGE_MARKETING_SLOGAN);
      }
    }

    if (taskAndCustomerHistoryActive) {
      authorities.add(PermissionAuthority.MANAGE_TASK_AND_CUSTOMER_HISTORY);
    }

    if (purchaseHistoryActive) {
      authorities.add(PermissionAuthority.MANAGE_PURCHASE_HISTORY);
    }

    if (usageHistoryActive) {
      authorities.add(PermissionAuthority.MANAGE_USAGE_HISTORY);
    }

    return authorities.stream()
        .map(PermissionAuthority::name)
        .toList();
  }
}
