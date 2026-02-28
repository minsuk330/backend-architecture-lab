package com.backend.lab.domain.purchase.core.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseMethod;
import com.backend.lab.domain.purchase.core.entity.vo.PurchaseStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Purchase extends BaseEntity {

  private Long memberId;
  private String name;
  @Enumerated(EnumType.STRING)
  private PurchaseStatus status;
  @Enumerated(EnumType.STRING)
  private PurchaseMethod method;
  private String orderNumber;
  private Integer totalPrice;

  private LocalDateTime purchasedAt;

  @OneToMany(mappedBy = "purchase", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  private Set<PurchaseItem> items = new HashSet<>();
}
