package com.backend.lab.api.common.property.gonsi.facade;

import com.backend.lab.api.admin.property.info.dto.resp.PropertyGonsiInfo;
import com.backend.lab.api.admin.property.info.dto.resp.PropertyGonsiInfoResp;
import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.common.openapi.dto.gonsiPrice.GonsiPriceApiResp;
import com.backend.lab.common.openapi.dto.gonsiPrice.GonsiPriceApiResp.GonsiPriceData;
import com.backend.lab.common.openapi.dto.gonsiPrice.GonsiPriceItem;
import com.backend.lab.common.openapi.service.tojiList.GonsiApiService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PropertyGonsiFacade {

  private final GonsiApiService gonsiApiService;

  //공시지가 조회
  public PropertyGonsiInfoResp getGonsi(String pnu) {
    CompletableFuture<List<PropertyGonsiInfo>> gonsiInfo = gonsiApiService.getGonsiInfo(pnu);
    try {
      List<PropertyGonsiInfo> propertyGonsiInfos = gonsiInfo.get();

      return PropertyGonsiInfoResp.builder()
          .data(propertyGonsiInfos)
          .build();

    } catch (InterruptedException | ExecutionException e) {
      throw new BusinessException(ErrorCode.valueOf("공시지가 정보 조회 중 오류가 발생했습니다."), e);
    }

  }

  public PageResp<PropertyGonsiInfo> getGinsiPage(String pnu, PageOptions options) {
    CompletableFuture<GonsiPriceApiResp> pageGonsiInfo = gonsiApiService.getPageGonsiInfo(pnu,
        options);
    try {
      GonsiPriceApiResp response = pageGonsiInfo.get();
      GonsiPriceData data = response.getIndvdLandPrices();

      int totalCount = Integer.parseInt(data.getTotalCount());
      int currentPage = Integer.parseInt(data.getPageNo()) - 1;
      int pageSize = Integer.parseInt(data.getNumOfRows());
      int totalPages = (int) Math.ceil((double) totalCount / pageSize);

      List<GonsiPriceItem> items = data.getField();
      
      GonsiPriceApiResp allDataForPercent = gonsiApiService.callLandCharacteristicsApi(pnu);
      List<GonsiPriceItem> allOriginalItems = allDataForPercent.getIndvdLandPrices().getField();
      
      List<PropertyGonsiInfo> list = items.stream()
          .map(item -> gonsiApiService.convertToPropertyLandInfo(item, allOriginalItems))
          .toList();

      return new PageResp<>(currentPage, pageSize, totalPages, totalCount, list);

    } catch (InterruptedException | ExecutionException e) {
      throw new BusinessException(ErrorCode.valueOf("공시지가 정보 조회 중 오류가 발생했습니다."), e);
    }

  }

}
