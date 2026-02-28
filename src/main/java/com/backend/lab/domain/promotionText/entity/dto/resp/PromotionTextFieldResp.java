package com.backend.lab.domain.promotionText.entity.dto.resp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PromotionTextFieldResp {
  private String placeholder; // <item_id>, <address> 등
  private Integer seq;// 순서
  private String prefixText;// 플레이스홀더 앞 텍스트 "✅️ 매물번호 : "
  private String suffixText;// 플레이스홀더 뒤 텍스트 " \n"

}
