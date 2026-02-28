package com.backend.lab.domain.agentItem.history.advItem.repository;

import com.backend.lab.domain.agentItem.history.advItem.entity.AdvertiseItemHistory;
import com.backend.lab.domain.agentItem.history.advItem.entity.dto.AdvertiseHistorySearchOptions;
import org.springframework.data.domain.Page;

public interface AdvertiseItemHistoryRepositoryCustom {

  Page<AdvertiseItemHistory> search(AdvertiseHistorySearchOptions options);
}
