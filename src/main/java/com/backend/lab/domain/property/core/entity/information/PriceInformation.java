package com.backend.lab.domain.property.core.entity.information;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.property.core.entity.embedded.PriceProperties;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "price_information")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PriceInformation extends BaseEntity {

  @Embedded
  private PriceProperties properties;
}
