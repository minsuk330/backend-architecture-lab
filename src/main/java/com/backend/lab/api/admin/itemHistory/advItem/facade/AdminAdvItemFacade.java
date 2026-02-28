package com.backend.lab.api.admin.itemHistory.advItem.facade;

import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.agentItem.history.advItem.entity.AdvertiseItemHistory;
import com.backend.lab.domain.agentItem.history.advItem.entity.dto.AdvertiseHistorySearchOptions;
import com.backend.lab.domain.agentItem.history.advItem.entity.dto.AdvertiseItemHistoryResp;
import com.backend.lab.domain.agentItem.history.advItem.service.AdvertiseItemHistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAdvItemFacade {

  private final AdvertiseItemHistoryService advertiseItemHistoryService;

  public PageResp<AdvertiseItemHistoryResp> search(AdvertiseHistorySearchOptions options) {
    Page<AdvertiseItemHistory> page = advertiseItemHistoryService.search(options);
    List<AdvertiseItemHistoryResp> data =page.getContent().stream()
        .map(advertiseItemHistoryService::advertiseHistoryResp)
        .toList();
    return new PageResp<>(page, data);
  }

}
