package com.backend.lab.domain.property.core.entity.embedded;

import com.backend.lab.domain.property.core.entity.vo.LedgerType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
@Schema(description = "건축물대장 정보")
public class LedgerProperties {

  private String pnu;
  @Schema(description = "지번 주소")
  private String jibunAddress;
  @Schema(description = "도로명 주소")
  private String roadAddress;
  @Schema(description = "동+지번")
  private String dongJibun;

  @Schema(description = "동 명")
  private String dongNm;

  @Schema(description = "최저층")
  private Integer minFloor; // 최저층
  @Schema(description = "최고층")
  private Integer maxFloor; // 최고층

  @Schema(description = "주용도")
  private String mainPurpsCdNm; // 주용도
  @Schema(description = "주구조")
  private String structure; // 주구조

  @Schema(description = "대지면적(㎡)")
  private Double landAreaMeter; // 대지면적(㎡)
  @Schema(description = "대지면적(평)")
  private Double landAreaPyeong; // 대지면적(평)

  @Schema(description = "건축면적(㎡)")
  private Double buildingAreaMeter; // 건축면적(㎡)
  @Schema(description = "건축면적(평)")
  private Double buildingAreaPyeong; // 건축면적(평)

  @Schema(description = "연면적(㎡)")
  private Double yeonAreaMeter; // 연면적(㎡)
  @Schema(description = "연면적(평)")
  private Double yeonAreaPyeong; // 연면적(평)

  @Schema(description = "용적률 산정 연면적(㎡)")
  private Double yongjeokAreaMeter; // 용적률 산정 연면적(㎡)
  @Schema(description = "용적률 산정 연면적(평)")
  private Double yongjeokAreaPyeong; // 용적률 산정 연면적(평)

  @Schema(description = "일반승강기 수")
  private Integer ilbanElevator; // 일반승강기 수
  @Schema(description = "화물승강기 수")
  private Integer hwamoolElevator; // 화물승강기 수

  @Schema(description = "도로너비(m)")
  private String doroWidthMeter; // 도로너비(m)

  @Schema(description = "자주식(옥내) 주차대수")
  private Integer jajuInParking; // 자주식(옥내) 주차대수
  @Schema(description = "자주식(옥외) 주차대수")
  private Integer jajuOutParking; // 자주식(옥외) 주차대수

  @Schema(description = "기계식(옥내) 주차대수")
  private Integer kigyeInParking; // 기계식(옥내) 주차대수
  @Schema(description = "기계식(옥외) 주차대수")
  private Integer kigyeOutParking; // 기계식(옥외) 주차대수

  @Schema(description = "전기차(옥내) 주차대수")
  private Integer elcInParking; // 전기차(옥내) 주차대수
  @Schema(description = "전기차(옥외) 주차대수")
  private Integer elcOutParking; // 전기차(옥외) 주차대수

  @Schema(description = "건폐율(%)")
  private Double geonPye; // 건폐율(%)
  @Schema(description = "용적률(%)")
  private Double yongjeok; // 용적률(%)

  @Schema(description = "기타 용도")
  private String etcUsage; // 기타 용도

  @Schema(description = "소유자")
  private String owner; // 소유자
  @Schema(description = "세대수")
  private Integer saedae; // 세대수
  @Schema(description = "가구수")
  private Integer gagu; // 가구수

  @Schema(description = "높이(m)")
  private Double height; // 높이(m)
  @Schema(description = "지붕구조")
  private String jiboong; // 지붕구조


  @Schema(description = "허가일자")
  private LocalDate heogaeDate; // 허가일자
  @Schema(description = "착공일자")
  private LocalDate chakGongDate; // 착공일자
  @Schema(description = "사용승인일자")
  private LocalDate sengInDate; // 사용승인일자
  @Schema(description = "리모델링일자")
  private LocalDate remodelDate; // 리모델링일자

  private Boolean yangmyen;
  private Boolean coner;

  @Enumerated(EnumType.STRING)
  private LedgerType type;

  public LedgerProperties update(LedgerProperties other) {
    if (other == null) {
      return this;
    }

    if (other.getMinFloor() != null) {
      this.minFloor = other.getMinFloor();
    }
    if (other.getMaxFloor() != null) {
      this.maxFloor = other.getMaxFloor();
    }
    if (other.getMainPurpsCdNm() != null) {
      this.mainPurpsCdNm = other.getMainPurpsCdNm();
    }
    if (other.getStructure() != null) {
      this.structure = other.getStructure();
    }
    if (other.getLandAreaMeter() != null) {
      this.landAreaMeter = other.getLandAreaMeter();
    }
    if (other.getLandAreaPyeong() != null) {
      this.landAreaPyeong = other.getLandAreaPyeong();
    }
    if (other.getBuildingAreaMeter() != null) {
      this.buildingAreaMeter = other.getBuildingAreaMeter();
    }
    if (other.getBuildingAreaPyeong() != null) {
      this.buildingAreaPyeong = other.getBuildingAreaPyeong();
    }
    if (other.getYeonAreaMeter() != null) {
      this.yeonAreaMeter = other.getYeonAreaMeter();
    }
    if (other.getYeonAreaPyeong() != null) {
      this.yeonAreaPyeong = other.getYeonAreaPyeong();
    }
    if (other.getYongjeokAreaMeter() != null) {
      this.yongjeokAreaMeter = other.getYongjeokAreaMeter();
    }
    if (other.getYongjeokAreaPyeong() != null) {
      this.yongjeokAreaPyeong = other.getYongjeokAreaPyeong();
    }
    if (other.getIlbanElevator() != null) {
      this.ilbanElevator = other.getIlbanElevator();
    }
    if (other.getHwamoolElevator() != null) {
      this.hwamoolElevator = other.getHwamoolElevator();
    }
    if (other.getDoroWidthMeter() != null) {
      this.doroWidthMeter = other.getDoroWidthMeter();
    }
    if (other.getJajuInParking() != null) {
      this.jajuInParking = other.getJajuInParking();
    }
    if (other.getJajuOutParking() != null) {
      this.jajuOutParking = other.getJajuOutParking();
    }
    if (other.getKigyeInParking() != null) {
      this.kigyeInParking = other.getKigyeInParking();
    }
    if (other.getKigyeOutParking() != null) {
      this.kigyeOutParking = other.getKigyeOutParking();
    }
    if (other.getElcInParking() != null) {
      this.elcInParking = other.getElcInParking();
    }
    if (other.getElcOutParking() != null) {
      this.elcOutParking = other.getElcOutParking();
    }
    if (other.getGeonPye() != null) {
      this.geonPye = other.getGeonPye();
    }
    if (other.getYongjeok() != null) {
      this.yongjeok = other.getYongjeok();
    }
    if (other.getEtcUsage() != null) {
      this.etcUsage = other.getEtcUsage();
    }
    if (other.getOwner() != null) {
      this.owner = other.getOwner();
    }
    if (other.getSaedae() != null) {
      this.saedae = other.getSaedae();
    }
    if (other.getGagu() != null) {
      this.gagu = other.getGagu();
    }
    if (other.getHeight() != null) {
      this.height = other.getHeight();
    }
    if (other.getJiboong() != null) {
      this.jiboong = other.getJiboong();
    }
    if (other.getHeogaeDate() != null) {
      this.heogaeDate = other.getHeogaeDate();
    }
    if (other.getChakGongDate() != null) {
      this.chakGongDate = other.getChakGongDate();
    }
    if (other.getSengInDate() != null) {
      this.sengInDate = other.getSengInDate();
    }
    if (other.getRemodelDate() != null) {
      this.remodelDate = other.getRemodelDate();
    }
    return this;
  }
}


