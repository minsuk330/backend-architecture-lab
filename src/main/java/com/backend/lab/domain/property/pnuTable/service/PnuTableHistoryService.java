package com.backend.lab.domain.property.pnuTable.service;

import com.backend.lab.domain.property.pnuTable.entity.PnuTableHistory;
import com.backend.lab.domain.property.pnuTable.repository.PnuHistoryRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PnuTableHistoryService {

  private final PnuHistoryRepository pnuHistoryRepository;

  public Page<PnuTableHistory> gets(Pageable pageable) {
    return pnuHistoryRepository.findAll(pageable);
  }

  @Transactional
  public void create(String fileName) {
    pnuHistoryRepository.save(
        PnuTableHistory.builder()
            .fileName(fileName)
            .createdAt(LocalDateTime.now())
            .build()
    );

  }
}
