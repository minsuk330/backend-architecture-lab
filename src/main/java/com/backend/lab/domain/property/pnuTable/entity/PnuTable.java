package com.backend.lab.domain.property.pnuTable.entity;

import com.backend.lab.domain.property.pnuTable.entity.vo.PnuTableType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
public class PnuTable {

  @Id
  private String pnu;

  @Enumerated(EnumType.ORDINAL)
  private PnuTableType type;

  private String sido;
  private String sidoName;
  private String sigungu;
  private String sigunguName;
  private String bjd;
  private String bjdName;
}
