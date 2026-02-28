package com.backend.lab.domain.purchase.item.entity.dto;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import com.backend.lab.domain.purchase.item.entity.vo.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResp extends BaseResp {

  private ProductType type;
  private String name;
  private Integer price;
  private Integer amount;
  private Boolean canPurchase;
}
