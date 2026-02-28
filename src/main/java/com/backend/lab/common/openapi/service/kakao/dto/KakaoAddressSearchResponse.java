package com.backend.lab.common.openapi.service.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoAddressSearchResponse {

  private List<KakaoDocument> documents;
  private KakaoMeta meta;

  @Data
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class KakaoMeta {
    @JsonProperty("total_count") private int totalCount;
    @JsonProperty("pageable_count") private int pageableCount;
    @JsonProperty("is_end") private boolean isEnd;
  }

  @Data
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class KakaoDocument {
    @JsonProperty("address_name") private String addressName;
    @JsonProperty("address_type") private String addressType;
    private String x;
    private String y;
    private KakaoAddress address;
    @JsonProperty("road_address") private KakaoRoadAddress roadAddress;
  }

  @Data
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class KakaoAddress {
    @JsonProperty("address_name") private String addressName;
    @JsonProperty("b_code") private String bCode;
    @JsonProperty("h_code") private String hCode;
    @JsonProperty("mountain_yn") private String mountainYn;
    @JsonProperty("main_address_no") private String mainAddressNo;
    @JsonProperty("sub_address_no") private String subAddressNo;
  }

  @Data
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class KakaoRoadAddress {
    @JsonProperty("address_name") private String addressName;
    @JsonProperty("zone_no") private String zoneNo;
    @JsonProperty("road_name") private String roadName;
    @JsonProperty("main_building_no") private String mainBuildingNo;
    @JsonProperty("sub_building_no") private String subBuildingNo;
    @JsonProperty("building_name") private String buildingName;
  }
}