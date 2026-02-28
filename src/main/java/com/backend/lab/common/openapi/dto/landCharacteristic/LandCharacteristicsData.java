package com.backend.lab.common.openapi.dto.landCharacteristic;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LandCharacteristicsData {
  private List<LandCharacteristicField> field;
  private String pageNo;
  private String resultCode;
  private String totalCount;
  private String numOfRows;
  private String resultMsg;
}