package com.backend.lab.domain.promotionText.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.promotionText.entity.vo.PromotionPlaceholder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "promotion_text_field")
public class PromotionTextField extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "promotion_text_id")
  private PromotionText promotionText;

  /** 출력용 접두사(예: "✅ 매물번호 : ") */
  @Column(nullable = false)
  private String prefix;

  ///뒤 /n 텍스트
  @Column(nullable = false)
  private String suffix;

  private Integer seq;

  @Enumerated(EnumType.STRING)
  private PromotionPlaceholder placeholder;



}
