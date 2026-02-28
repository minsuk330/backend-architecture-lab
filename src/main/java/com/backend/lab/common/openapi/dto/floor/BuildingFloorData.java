package com.backend.lab.common.openapi.dto.floor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildingFloorData {

  private Integer rnum;

  private String platPlc; // 대지위치

  private String sigunguCd; // 시군구코드

  private String bjdongCd; // 법정동코드

  private String platGbCd; // 대지구분코드

  private String bun; // 번

  private String ji; // 지

  private String mgmBldrgstPk; // 관리건축물대장PK

  private String newPlatPlc; // 새주소 대지위치

  private String bldNm; // 건물명

  private String splotNm; // 특수지명

  private String block; // 블록

  private String lot; // 로트

  private String naRoadCd; // 새주소도로코드

  private String naBjdongCd; // 새주소법정동코드

  private String naUgrndCd; // 새주소지상지하코드

  private String naMainBun; // 새주소본번

  private String naSubBun; // 새주소부번

  private String dongNm; // 동명칭

  private String flrGbCd; // 층구분코드

  private String flrGbCdNm; // 층구분코드명

  private Integer flrNo; // 층번호

  private String flrNoNm; // 층번호명

  private String strctCd; // 구조코드

  private String strctCdNm; // 구조코드명

  private String etcStrct; // 기타구조

  private String mainPurpsCd; // 주용도코드

  private String mainPurpsCdNm; // 주용도코드명

  private String etcPurps; // 기타용도

  private String mainAtchGbCd; // 주부속구분코드

  private String mainAtchGbCdNm; // 주부속구분코드명

  private Double area; // 면적

  private String areaExctYn; // 면적제외여부

  private String crtnDay; // 생성일시
}