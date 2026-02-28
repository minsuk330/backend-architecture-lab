package com.backend.lab.domain.property.pnuTable.repository;

import com.backend.lab.domain.property.pnuTable.entity.PnuTableHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PnuHistoryRepository extends JpaRepository<PnuTableHistory, Long> {

  Page<PnuTableHistory> findAll(Pageable pageable);
}
