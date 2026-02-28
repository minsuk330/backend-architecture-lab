package com.backend.lab.common.openapi.dto.ledger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildingLedgerData {

  private String platPlc;           // 소재지(지번) → dongJi 매핑용 jibunaddress로 가져오고
  private String newPlatPlc;        // 소재지(도로명)
  private String bldNm;             // 건물명
  private String dongNm;            // 동명칭 → dongName 매핑용
  private String mgmBldrgstPk;

  // 용도 및 구조 - LedgerProperties 매핑
  private String mainPurpsCdNm;     // 주용도 → usage
  private String etcPurps;          // 기타용도 → etcUsage
  private String strctCdNm;         // 주구조 → structure
  private String roofCdNm;          // 지붕구조 → jiboong

  // 면적 정보 - LedgerProperties 매핑
  private Double platArea;          // 대지면적(㎡) → landAreaMeter
  private Double archArea;          // 건축면적(㎡) → buildingAreaMeter
  private Double totArea;           // 연면적(㎡) → yeonAreaMeter
  private Double vlRatEstmTotArea;  // 용적률산정연면적(㎡) → yongjeokAreaMeter

  // 층수 및 높이 - LedgerProperties 매핑
  private String grndFlrCnt;        // 지상층수 → maxFloor
  private String ugrndFlrCnt;       // 지하층수 → minFloor (음수 변환)
  private String heit;              // 높이(m) → height

  // 세대 및 가구 - LedgerProperties 매핑
  private String hhldCnt;           // 세대수 → saedae
  private String fmlyCnt;           // 가구수 → gagu

  // 승강기 - LedgerProperties 매핑
  private String rideUseElvtCnt;    // 승용승강기수 → ilbanElevator
  private String emgenUseElvtCnt;   // 비상용승강기수 → hwamoolElevator

  // 주차시설 - LedgerProperties 매핑
  private String indrAutoUtcnt;     // 옥내자주식대수 → jajuInParking
  private String oudrAutoUtcnt;     // 옥외자주식대수 → jajuOutParking
  private String indrMechUtcnt;     // 옥내기계식대수 → kigyeInParking
  private String oudrMechUtcnt;     // 옥외기계식대수 → kigyeOutParking

  // 비율 - LedgerProperties 매핑
  private String bcRat;             // 건폐율(%) → geonPye
  private String vlRat;             // 용적률(%) → yongjeok

  // 일자 정보 - LedgerProperties 매핑
  private String pmsDay;            // 허가일자 → heogaeDate
  private String stcnsDay;          // 착공일자 → chakGongDate
  private String useAprDay;         // 사용승인일자 → sengInDate

}
