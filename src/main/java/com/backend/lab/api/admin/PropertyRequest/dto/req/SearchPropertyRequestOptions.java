package com.backend.lab.api.admin.PropertyRequest.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.domain.propertyRequest.entity.vo.RequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
public class SearchPropertyRequestOptions extends PageOptions {

  private String query;
  private RequestStatus status;

}
