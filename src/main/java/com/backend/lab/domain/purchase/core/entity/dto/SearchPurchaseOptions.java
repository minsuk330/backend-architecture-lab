package com.backend.lab.domain.purchase.core.entity.dto;

import com.backend.lab.common.entity.dto.req.PageOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchPurchaseOptions extends PageOptions {

  private String query;
  private Long productId;
}
