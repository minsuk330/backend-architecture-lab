package com.backend.lab.domain.admin.permission.entity.dto.req;

import com.backend.lab.domain.admin.permission.entity.vo.SecretMemoExposeLevel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PermissionUpdateReq {

  @NotNull
  private Integer rank;

  @NotEmpty
  private String name;
  private Boolean active; // 권한 활성화 여부
  private Long migrationPermissionId; // 권한 삭제 시, 마이그레이션을 위한 ID

//  private Boolean maemoolActive; // 매물 관련 권한 활성화 여부
  private Boolean canManageMaemool; // 매물 관리
  private SecretMemoExposeLevel secretMemoExposeLevel; // 비밀 메모 노출 레벨

  @NotNull
  private Boolean interestActive; // 관심 매물 관련 권한 활성화 여부
  private Boolean canManageInterestGroup; // 관심 그룹 관리

  @NotNull
  private Boolean requestRegisterActive; // 매물 등록 요청 관련 권한
  private Boolean canManageState; // 상태 관리

  @NotNull
  private Boolean customerActive; // 고객 관련 권한
  private Boolean canManageCustomer; // 고객 관리

  @NotNull
  private Boolean boardActive; // 게시판 관련 권한
  private Boolean canManageNotice; // 공지사항 관리
  private Boolean canManageEvent; // 이벤트 관리
  private Boolean canManagePopup; // 팝업 관리
  private Boolean canManageTodayNews; // 오늘의 뉴스 관리

  @NotNull
  private Boolean homePageActive; // 홈페이지 관련 권한
  private Boolean canManageCategory; // 카테고리 관리
  private Boolean canManagePrint; // 프린트 설정
  private Boolean canManageMarketingThumbnail; // 마케팅 썸네일 설정
  private Boolean canManageMarketingSlogan; // 홍보문구 설정

  @NotNull
  private Boolean taskAndCustomerHistoryActive; // 작업/고객 이력 관련 권한

  @NotNull
  private Boolean purchaseHistoryActive; // 결제 내역 관련 권한}
  private Boolean usageHistoryActive; // 사용 내역 관련 권한


}