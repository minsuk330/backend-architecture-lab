package com.backend.lab.domain.promotionText.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.promotionText.entity.vo.PromotionMemberType;
import com.backend.lab.domain.promotionText.entity.vo.PromotionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "promotion_text")
public class PromotionText extends BaseEntity {

  @Enumerated(EnumType.STRING)
  private PromotionType type;

  @Enumerated(EnumType.STRING)
  private PromotionMemberType memberType;

  private Long agentId;

  @OneToMany(mappedBy = "promotionText", cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
  @OrderBy("seq ASC")
  @Builder.Default
  private List<PromotionTextField> fields = new ArrayList<>();


  public void addField(PromotionTextField field) {
    fields.add(field);
    field.setPromotionText(this);
  }

  public void removeField(PromotionTextField field) {
    fields.remove(field);
    field.setPromotionText(null);
  }

}
