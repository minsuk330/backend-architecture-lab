package com.backend.lab.domain.purchase.core.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.purchase.item.entity.PurchaseProduct;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class PurchaseItem extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "purchase_id", nullable = false)
  private Purchase purchase;

  public void setPurchase(Purchase purchase) {
    this.purchase = purchase;
    if (!purchase.getItems().contains(this)) {
      purchase.getItems().add(this);
    }
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private PurchaseProduct product;
  private Integer amount;
  private Integer totalPrice;
}
