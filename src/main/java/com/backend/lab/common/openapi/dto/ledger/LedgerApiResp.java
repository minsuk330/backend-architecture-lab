package com.backend.lab.common.openapi.dto.ledger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public class LedgerApiResp {
    private Response response;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
      private Header header;
      private Body body;
    }

    @Data
    public static class Header {
      private String resultCode;
      private String resultMsg;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
      private Items items;
      private int totalCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
      private List<BuildingLedgerData> item;
    }
  }

