package com.backend.lab.domain.property.propertyWorkLog.entity.dto.req;

import com.backend.lab.common.entity.dto.req.PageOptions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@NoArgsConstructor
@ParameterObject
public class PropertyWorkLogDetailsOptions extends PageOptions {

  private Long propertyId;

}
