package com.backend.lab.api.admin.wishlist.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@ParameterObject
@Setter
@NoArgsConstructor
public class SearchWishlistOptions extends PageOptions {

  private String query;
  private Integer minInterestCount;
  private Integer maxInterestCount;

}
