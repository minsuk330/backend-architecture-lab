package com.backend.lab.domain.wishlistGroup.entity.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistGroupCreateReq {
  private String groupName;
  private Integer minInterestCount;
  private Integer maxInterestCount;

}
