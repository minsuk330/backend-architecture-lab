package com.backend.lab.domain.agentItem.history.advItem.repository;

import com.backend.lab.domain.agentItem.history.advItem.entity.AdvertiseItemHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvertiseItemHistoryRepository extends JpaRepository<AdvertiseItemHistory, Long>, AdvertiseItemHistoryRepositoryCustom {

}
