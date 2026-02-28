package com.backend.lab.domain.admin.permission.entity.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.admin.permission.entity.vo.SecretMemoExposeLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResp extends BaseResp {

  private Integer rank;
  private String name; // 권한명
  private boolean isActive; // 권한 활성화 여부
  private Long migrationPermissionId; // 권한 삭제 시, 마이그레이션을 위한 ID
  @Setter
  private String migrationPermissionName;

  private boolean isMaemoolActive; // 매물 관련 권한 활성화 여부
  private boolean canManageMaemool; // 매물 관리
  private SecretMemoExposeLevel secretMemoExposeLevel; // 비밀 메모 노출 레벨

  private boolean isInterestActive; // 관심 매물 관련 권한 활성화 여부
  private boolean canManageInterestGroup; // 관심 그룹 관리

  private boolean isRequestRegisterActive; // 매물 등록 요청 관련 권한
  private boolean canManageState; // 상태 관리

  private boolean isCustomerActive; // 고객 관련 권한
  private boolean canManageCustomer; // 고객 관리

  private boolean isBoardActive; // 게시판 관련 권한
  private boolean canManageNotice; // 공지사항 관리
  private boolean canManageEvent; // 이벤트 관리
  private boolean canManagePopup; // 팝업 관리
  private boolean canManageTodayNews; // 오늘의 뉴스 관리

  private boolean isHomePageActive; // 홈페이지 관련 권한
  private boolean canManageCategory; // 카테고리 관리
  private boolean canManagePrint; // 프린트 설정
  private boolean canManageMarketingThumbnail; // 마케팅 썸네일 설정
  private boolean canManageMarketingSlogan; // 홍보문구 설정

  private boolean isTaskAndCustomerHistoryActive; // 작업/고객 이력 관련 권한

  private boolean isPurchaseHistoryActive; // 결제 내역 관련 권한
  private boolean isUsageHistoryActive;
}
