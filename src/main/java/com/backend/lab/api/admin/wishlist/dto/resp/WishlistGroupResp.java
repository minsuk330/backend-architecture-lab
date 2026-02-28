package com.backend.lab.api.admin.wishlist.dto.resp;

import com.backend.lab.common.entity.dto.resp.BaseResp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class WishlistGroupResp extends BaseResp {
  private String groupName;
  private Integer minInterestCount;
  private Integer maxInterestCount;
}
