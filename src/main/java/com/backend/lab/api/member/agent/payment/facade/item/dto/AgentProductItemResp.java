package com.backend.lab.api.member.agent.payment.facade.item.dto;

import com.backend.lab.domain.purchase.item.entity.dto.ProductResp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentProductItemResp {

  private List<ProductResp> products;
}
