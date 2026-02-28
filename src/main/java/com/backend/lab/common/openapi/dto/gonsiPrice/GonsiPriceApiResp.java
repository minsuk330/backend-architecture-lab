package com.backend.lab.common.openapi.dto.gonsiPrice;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GonsiPriceApiResp {

  @JsonProperty("indvdLandPrices")
  private GonsiPriceData indvdLandPrices;

  @Getter
  @Setter
  public static class GonsiPriceData {

    @JsonProperty("field")
    private List<GonsiPriceItem> field;

    @JsonProperty("pageNo")
    private String pageNo;

    @JsonProperty("resultCode")
    private String resultCode;

    @JsonProperty("totalCount")
    private String totalCount;

    @JsonProperty("numOfRows")
    private String numOfRows;

    @JsonProperty("resultMsg")
    private String resultMsg;
  }
}
