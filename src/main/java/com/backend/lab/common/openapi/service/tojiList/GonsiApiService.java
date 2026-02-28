package com.backend.lab.common.openapi.service.tojiList;

import com.backend.lab.api.admin.property.info.dto.resp.PropertyGonsiInfo;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.openapi.dto.gonsiPrice.GonsiPriceApiResp;
import com.backend.lab.common.openapi.dto.gonsiPrice.GonsiPriceItem;
import java.net.URI;
import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GonsiApiService {

  private final WebClient webClient;

  @Value("${building-register.api.gonsiBaseUrl}")
  private String baseUrl;


  @Value("${building-register.api.vworldKey}")
  private String apiKey;

  @Async("buildingExecutor")
  public CompletableFuture<List<PropertyGonsiInfo>> getGonsiInfo(String pnu) {
    GonsiPriceApiResp gonsiPriceApiResp = callLandCharacteristicsApi(pnu);

    List<GonsiPriceItem> items = gonsiPriceApiResp.getIndvdLandPrices().getField();
    List<PropertyGonsiInfo> list = items.stream()
        .map(item -> convertToPropertyLandInfo(item, items))
        .collect(Collectors.groupingBy(
            PropertyGonsiInfo::getStdryear,
            LinkedHashMap::new,
            Collectors.toList()
        ))
        .values()
        .stream()
        .map(group -> group.get(0))
        .sorted(Comparator.comparing(PropertyGonsiInfo::getStdryear, Comparator.reverseOrder()))
        .toList();

    return CompletableFuture.completedFuture(list);
  }

  @Async("buildingExecutor")
  public CompletableFuture<GonsiPriceApiResp> getPageGonsiInfo(String pnu, PageOptions options) {
    GonsiPriceApiResp allDataResp = callLandCharacteristicsApi(pnu);
    
    List<GonsiPriceItem> allItems = allDataResp.getIndvdLandPrices().getField();
    List<GonsiPriceItem> processedItems = allItems.stream()
        .collect(Collectors.groupingBy(
            GonsiPriceItem::getStdrYear,
            LinkedHashMap::new,
            Collectors.toList()
        ))
        .values()
        .stream()
        .map(group -> group.get(0))
        .sorted(Comparator.comparing(GonsiPriceItem::getStdrYear, Comparator.reverseOrder()))
        .toList();
    
    int pageSize = options.pageable().getPageSize();
    int pageNumber = options.pageable().getPageNumber();
    int startIndex = pageNumber * pageSize;
    int endIndex = Math.min(startIndex + pageSize, processedItems.size());
    
    List<GonsiPriceItem> pagedItems = processedItems.subList(startIndex, endIndex);
    
    GonsiPriceApiResp response = new GonsiPriceApiResp();
    response.setIndvdLandPrices(allDataResp.getIndvdLandPrices());
    response.getIndvdLandPrices().setField(pagedItems);
    response.getIndvdLandPrices().setTotalCount(String.valueOf(processedItems.size()));
    response.getIndvdLandPrices().setPageNo(String.valueOf(pageNumber + 1));
    response.getIndvdLandPrices().setNumOfRows(String.valueOf(pageSize));
    
    return CompletableFuture.completedFuture(response);
  }

  private GonsiPriceApiResp callPageApi(String pnu, PageOptions options) {
    // 브이월드 API 예제 구조에 맞춤

    Pageable pageable = options.pageable();

    String fullUrl = UriComponentsBuilder.fromUri(URI.create(baseUrl))
        .queryParam("key", apiKey)
        .queryParam("domain", "www.kmorgan.co.kr")  // 도메인 값 필요시 설정
        .queryParam("pnu", pnu)
        .queryParam("stdrYear", "")
        .queryParam("format", "json")
        .queryParam("numOfRows", pageable.getPageSize())
        .queryParam("pageNo", pageable.getPageNumber() + 1) // 요 api는 페이지네이션이 1부터 시작
        .toUriString();

    GonsiPriceApiResp response = webClient.get()
        .uri(fullUrl)
        .retrieve()
        .bodyToMono(GonsiPriceApiResp.class)
        .timeout(Duration.ofSeconds(10))
        .block();

    if (response == null) {
      throw new RuntimeException("토지 특성 정보 API 응답이 null입니다: PNU=" + pnu);
    }

    return response;
  }

  public GonsiPriceApiResp callLandCharacteristicsApi(String pnu) {
    // 브이월드 API 예제 구조에 맞춤
    String fullUrl = UriComponentsBuilder.fromUri(URI.create(baseUrl))
        .queryParam("key", apiKey)
        .queryParam("domain", "kmorgan.co.kr")
        .queryParam("pnu", pnu)
        .queryParam("stdrYear", "")
        .queryParam("format", "json")
        .queryParam("numOfRows", "1000")
        .queryParam("pageNo", "1")
        .toUriString();

    GonsiPriceApiResp response = webClient.get()
        .uri(fullUrl)
        .retrieve()
        .bodyToMono(GonsiPriceApiResp.class)
        .timeout(Duration.ofSeconds(10))
        .block();

    if (response == null) {
      throw new RuntimeException("토지 특성 정보 API 응답이 null입니다: PNU=" + pnu);
    }

    return response;
  }
  public PropertyGonsiInfo convertToPropertyLandInfo(GonsiPriceItem gonsiPriceItem, List<GonsiPriceItem> allItems) {
    if (gonsiPriceItem == null) {
      return null;
    }

    String publicNotificationPrice = gonsiPriceItem.getPblntfPclnd();
    if (!StringUtils.hasText(publicNotificationPrice)) {
      publicNotificationPrice = "";
    }

    String stdryear = formatDate(gonsiPriceItem.getStdrYear());
    if (!StringUtils.hasText(stdryear)) {
      stdryear = "";
    }

    // 이전 년도 대비 증감률 계산
    String percentChange = calculatePercentChange(gonsiPriceItem, allItems);

    return PropertyGonsiInfo.builder()
        .gonsiPrice(publicNotificationPrice)
        .stdryear(stdryear)
        .percent(percentChange)
        .build();
  }

  private String calculatePercentChange(GonsiPriceItem currentItem, List<GonsiPriceItem> allItems) {
    if (currentItem == null || !StringUtils.hasText(currentItem.getStdrYear()) 
        || !StringUtils.hasText(currentItem.getPblntfPclnd())) {
      return "";
    }

    try {
      int currentYear = Integer.parseInt(currentItem.getStdrYear());
      double currentPrice = Double.parseDouble(currentItem.getPblntfPclnd());
      
      // 이전 년도 데이터 찾기
      GonsiPriceItem previousYearItem = allItems.stream()
          .filter(item -> StringUtils.hasText(item.getStdrYear()) && StringUtils.hasText(item.getPblntfPclnd()))
          .filter(item -> {
            try {
              int itemYear = Integer.parseInt(item.getStdrYear());
              return itemYear == currentYear - 1;
            } catch (NumberFormatException e) {
              return false;
            }
          })
          .findFirst()
          .orElse(null);

      if (previousYearItem == null) {
        return ""; // 이전 년도 데이터 없음
      }

      double previousPrice = Double.parseDouble(previousYearItem.getPblntfPclnd());
      
      if (previousPrice == 0) {
        return ""; // 0으로 나누기 방지
      }

      // 증감률 계산: ((현재가격 - 이전가격) / 이전가격) * 100
      double percentChange = ((currentPrice - previousPrice) / previousPrice) * 100;
      
      // 소수점 1자리까지 표시, 양수일 때 + 기호 추가
      if (percentChange > 0) {
        return String.format("+%.1f%%", percentChange);
      } else {
        return String.format("%.1f%%", percentChange);
      }

    } catch (NumberFormatException e) {
      return ""; // 숫자 변환 오류
    }
  }

  private String formatDate(String dateStr) {
    if (!StringUtils.hasText(dateStr)) {
      return "";
    }

    try {
      if (dateStr.contains("-")) {
        return dateStr.replace("-", ".");
      }
      return dateStr;
    } catch (Exception e) {
      return dateStr;
    }
  }

}
