package com.backend.lab.domain.property.pnuTable.entity.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

@ParameterObject
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PnuSearchOptions {

  private String sidoCode;
  private String sigunguCode;
}
