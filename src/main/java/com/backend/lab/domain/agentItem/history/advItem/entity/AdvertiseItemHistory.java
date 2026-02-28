package com.backend.lab.domain.agentItem.history.advItem.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.propertyAdvertisement.entity.PropertyAdvertisement;
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
public class AdvertiseItemHistory extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "problem_advertisement_id")
  private PropertyAdvertisement propertyAdvertisement;

  private Integer beforeCount;
  private Integer afterCount;
}
