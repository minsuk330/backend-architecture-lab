package com.backend.lab.domain.property.sale.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sale")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Sale extends BaseEntity {

  private LocalDate saleAt; // 매각일
  private Long memberId; // 계약자 id
  private Long propertyId; // 매각 대상 부동산 id
  private String contractorName;

  private Long salePrice; // 매각가
  private Long earningPrice; // 실적금액

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "contract_id")
  private UploadFile contract;//계약서
}
