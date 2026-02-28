package com.backend.lab.common.openapi.dto.landMove;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LandMoveField {
  private String ladMvmnDe;            // 토지이동일자
  private String ladMvmnPrvonshCodeNm; // 토지이동사유코드명
}
