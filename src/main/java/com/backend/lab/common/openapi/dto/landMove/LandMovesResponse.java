package com.backend.lab.common.openapi.dto.landMove;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LandMovesResponse {
  private LandMovesData landMoves;

}
