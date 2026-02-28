package com.backend.lab.domain.purchase.item.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.purchase.item.entity.vo.ProductType;
import jakarta.persistence.Entity;
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
public class PurchaseProduct extends BaseEntity {

  private ProductType type;
  private String name;
  private Integer price;
  private Integer amount;
}
