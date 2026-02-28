package com.backend.lab.domain.promotionText.entity.vo;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromotionPlaceholder {
  BUILDING_REGISTER("<building_register>"),
  IS_VIOLATE("<is_violate>"),

  /* ───── 기본 매물 키 ───── */
  ITEM_ID("<item_id>"),
  ADDRESS("<address>"),
  ITEM_CATEGORY("<item_category>"),

  /* ───── 가격·규모 ───── */
  ITEM_PRICE("<item_price>"),
  ITEM_AREA("<item_area>"),
  MANAGE("<manage>"),
  FLOOR("<floor>"),

  /* ───── 부가 시설 ───── */
  PARK_YN("<park_yn>"),
  ELEV("<elev>"),

  /* ───── 토지·용도 관련 ───── */
  LAND_USE("<land_use>"),
  CURRENT_CATEGORY("<current_category>"),
  MAIN_YONGDO("<main_yongdo>"),
  JIMOK("<jimok>"),

  /* ───── 날짜 ───── */
  APP_DATE("<appdate>"),

  /* ───── 세부 속성 ───── */
  CONTENT("<content>");

  private final String token;

  /* ---------- 역매핑 ---------- */
  private static final Map<String, PromotionPlaceholder> LOOKUP =
      Arrays.stream(values())
          .collect(Collectors.toMap(PromotionPlaceholder::getToken,
              Function.identity()));

  /** 〈item_id〉 같은 토큰 문자열을 Enum 으로 변환   */
  public static PromotionPlaceholder of(String token) {
    PromotionPlaceholder ph = LOOKUP.get(token);
    if (ph == null) {
      throw new IllegalArgumentException("알 수 없는 토큰: " + token);
    }
    return ph;
  }
}
