package com.backend.lab.domain.wishlistGroup.entity;

import com.backend.lab.common.entity.BaseEntity;
import jakarta.persistence.Entity;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "wishlist_group")
public class WishlistGroup extends BaseEntity {

  private String groupName;
  private Integer minInterestCount;
  private Integer maxInterestCount;
}
