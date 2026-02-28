package com.backend.lab.api.admin.promotionText.facade;

import com.backend.lab.api.admin.promotionText.dto.PromotionTextResp;
import com.backend.lab.domain.details.entity.Details;
import com.backend.lab.domain.promotionText.entity.PromotionTextField;
import com.backend.lab.domain.promotionText.entity.vo.PromotionPlaceholder;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.embedded.LedgerProperties;
import com.backend.lab.domain.property.core.entity.information.FloorInformation;
import com.backend.lab.domain.property.core.entity.information.LandInformation;
import com.backend.lab.domain.property.core.entity.information.LedgeInformation;
import com.backend.lab.domain.property.core.entity.vo.LedgerType;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionMapper {

  public String placeholderToValue(PromotionPlaceholder ph, Property p, Details details) {

    /* ─ 공통 헬퍼 ─ */
    LedgeInformation firstLedge = p.getLedge().isEmpty() ? null : p.getLedge().get(0);
    LedgerProperties ledgeProp  = firstLedge != null ? firstLedge.getProperties() : null;
    LandInformation firstLand  = p.getLand().isEmpty()  ? null : p.getLand().get(0);

    FloorInformation floor = p.getFloors().isEmpty() ? null : p.getFloors().iterator().next();
    switch (ph) {

      /* ───────── 상태/등기/위반 ───────── */
      case BUILDING_REGISTER -> {          // 미등기 건물
        if (ledgeProp != null &&
            LedgerType.UNREGISTERED.equals(ledgeProp.getType())) {
          return "미등기 건물";
        }
        return "";                       // 값이 없으면 줄 숨김
      }
      case IS_VIOLATE -> {                 // 위반건축물
        if (ledgeProp != null &&
            LedgerType.UNAUTHORIZED.equals(ledgeProp.getType())) {
          return "위반 건축물";
        }
        return "";
      }

      /* ───────── 기본 키 정보 ───────── */
      case ITEM_ID      ->  { return p.getId() != null ? p.getId().toString() : ""; }
      case ADDRESS      ->  { return p.getAddress() != null
          ? p.getAddress().getProperties().getJibunAddress()
          : ""; }
      case ITEM_CATEGORY->  { return "매매/" + p.getBigCategory().getName(); }

      /* ───────── 가격 ───────── */
      case ITEM_PRICE ->  {                 // “매매 195억 / 평단가 1억5,730만” 형식
        if (p.getPrice() == null) return "";
        var prop = p.getPrice().getProperties();
        return String.format("매매 %s억/평단가 %s만",
            formatWonToEok(prop.getMmPrice()),
            formatWonToMan(prop.getPyeongPrice()));
      }
      case MANAGE ->  {                     // 관리비
        return p.getPrice() != null
            ? formatWon(p.getPrice().getProperties().getGrPrice())
            : "";
      }

      /* ───────── 면적/층/주차/엘리베이터 ───────── */
      case ITEM_AREA -> {                   // 대지/연/건축
        if (firstLand == null || ledgeProp == null) return "";
        String land  = formatArea(firstLand.getProperties().getAreaMeter(),
            firstLand.getProperties().getAreaPyung(), "대지");
        String yeon  = formatArea(ledgeProp.getYeonAreaMeter(),
            ledgeProp.getYeonAreaPyeong(), "연");
        String geon  = formatArea(ledgeProp.getBuildingAreaMeter(),
            ledgeProp.getBuildingAreaPyeong(), "건축");
        return Stream.of(land, yeon, geon)
            .filter(s -> !s.isBlank())
            .collect(Collectors.joining("/"));
      }
      case FLOOR -> {                       // “B1/6F”
        if (ledgeProp == null) return "";
        return ledgeProp.getMinFloor() + "/" + ledgeProp.getMaxFloor(); }
      case PARK_YN -> {                     // 총 주차대수
        if (ledgeProp == null) return "";
        int total = sum(
            ledgeProp.getJajuOutParking(),
            ledgeProp.getJajuInParking(),
            ledgeProp.getElcInParking(),
            ledgeProp.getKigyeOutParking(),
            ledgeProp.getKigyeInParking(),
            ledgeProp.getElcOutParking());
        return total > 0 ? "총 " + total + "대" : ""; }
      case ELEV -> {                        // 일반 or 화물
        if (ledgeProp == null) return "";
        Integer gen = ledgeProp.getIlbanElevator();
        Integer fr  = ledgeProp.getHwamoolElevator();
        if (gen != null && gen > 0) return "일반" + gen + "대";
        if (fr  != null && fr  > 0) return "화물" + fr  + "대";
        return "";
      }

      /* ───────── 용도/날짜/지목 ───────── */
      case LAND_USE          -> { return firstLand != null
          ? firstLand.getProperties().getYongdo() : ""; }
      case MAIN_YONGDO       -> { return ledgeProp != null
          ? ledgeProp.getMainPurpsCdNm() : ""; }
      case APP_DATE           -> { return ledgeProp != null && ledgeProp.getSengInDate()!=null
          ? ledgeProp.getSengInDate().toString() : ""; }
      case JIMOK             -> { return firstLand != null
          ? firstLand.getProperties().getJimok() : ""; }

      case CONTENT -> {
        return details.getContent();
      }
      case CURRENT_CATEGORY -> {
        String upjong = floor == null || floor.getProperties() == null ? null : floor.getProperties().getUpjong();
        return upjong != null ? upjong : "";
      }

      default -> { return ""; }
    }
  }

  private String formatWonToEok(Long won)   { return won == null? "" : String.format("%,d", won/100000000); }
  private String formatWonToMan(Long won)   { return won == null? "" : String.format("%,d", won/10000); }
  private String formatWon(Long won)        { return won == null? "" : String.format("%,d원", won); }

  /* ㎡ → 평 동시 출력  "대지 409.8㎡(123.9평)"  */
  private String formatArea(Double m2, Double py, String label) {
    return (m2==null && py==null) ? ""
        : label + " " + (m2!=null? String.format("%.1f㎡", m2):"")
            + (py!=null? String.format("(%,.2f평)", py):"");
  }

  /* NPE 방지 합계 */
  private int sum(Integer... nums) {
    return Arrays.stream(nums).filter(Objects::nonNull).mapToInt(i->i).sum();
  }


  public PromotionTextResp promotionTextResp(String value, PromotionTextField field) {
    return PromotionTextResp.builder()
        .value(value)
        .seq(field.getSeq())
        .suffixText(field.getSuffix())
        .prefixText(field.getPrefix())
        .build();
  }
}
