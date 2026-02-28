package com.backend.lab.domain.propertyRequest.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestStatus;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestType;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "property_request")
public class PropertyRequest extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requester_id")
  private Member requester;

  private String nonMemberName;
  private String nonMemberPhoneNumber;

  @Enumerated(EnumType.STRING)
  private RequestType type;

  @Enumerated(EnumType.STRING)
  private RequestStatus status;

  // 매물 기본 정보
  private String buildingName;
  private String jibunAddress;
  private String roadAddress;

  // 가격 정보
  private Integer mmPrice;
  private Integer depositPrice;
  private Integer monthPrice;

  @JoinColumn(name = "exclusiveContract_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UploadFile exclusiveContract;

  // 승인/거부 정보
  private Long approvedByAdminId; // 승인한 관리자
  private LocalDateTime approvedAt;
  private LocalDateTime rejectedAt;


  // 승인 후 생성된 매물 ID
  private Long createdPropertyId;
}
