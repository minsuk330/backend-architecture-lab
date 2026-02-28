package com.backend.lab.common.openapi.dto.landPossession;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LandPossessionData {
  private List<LandPossessionField> field;
  private String pageNo;
  private String resultCode;
  private String totalCount;
  private String numOfRows;
  private String resultMsg;
}