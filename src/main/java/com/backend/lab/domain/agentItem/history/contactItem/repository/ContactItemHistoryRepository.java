package com.backend.lab.domain.agentItem.history.contactItem.repository;

import com.backend.lab.domain.agentItem.history.contactItem.entity.ContactItemHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactItemHistoryRepository extends JpaRepository<ContactItemHistory, Long>, ContactItemHistoryRepositoryCustom {

  Optional<ContactItemHistory> findByPropertyIdAndAgentId(Long propertyId, Long userId);
}
