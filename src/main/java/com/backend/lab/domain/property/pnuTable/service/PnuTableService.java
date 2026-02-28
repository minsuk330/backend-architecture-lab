package com.backend.lab.domain.property.pnuTable.service;

import com.backend.lab.domain.property.pnuTable.entity.PnuTable;
import com.backend.lab.domain.property.pnuTable.entity.dto.req.PnuCreateReq;
import com.backend.lab.domain.property.pnuTable.entity.dto.resp.PnuResp;
import com.backend.lab.domain.property.pnuTable.entity.vo.PnuTableType;
import com.backend.lab.domain.property.pnuTable.repository.PnuTableRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PnuTableService {

  private final PnuTableRepository pnuTableRepository;

  @Transactional
  public List<PnuTable> gets() {
    return pnuTableRepository.findAll();
  }

  @Transactional
  public void saveAll(List<PnuCreateReq> batchInsertList) {
    if (batchInsertList.isEmpty()) {
      return;
    }

    // 1. 모든 PNU를 한 번에 조회 (SELECT ... WHERE pnu IN (...))
    List<String> pnuList = batchInsertList.stream()
        .filter(Objects::nonNull)
        .map(PnuCreateReq::getPnu)
        .collect(Collectors.toList());

    List<PnuTable> existingPnuTables = pnuTableRepository.findAllByPnuIn(pnuList);

    // 2. 기존 PNU들을 Map으로 변환 (빠른 조회를 위해)
    Map<String, PnuTable> existingPnuMap = existingPnuTables.stream()
        .collect(Collectors.toMap(PnuTable::getPnu, Function.identity()));

    // 3. 업데이트할 항목과 새로 생성할 항목 분리
    List<PnuTable> toUpdate = new ArrayList<>();
    List<PnuTable> toInsert = new ArrayList<>();

    for (PnuCreateReq req : batchInsertList) {
      if (req.getSido() == null) {
        throw new IllegalStateException("sido is null");
      }

      PnuTable existingPnu = existingPnuMap.get(req.getPnu());

      if (existingPnu != null) {
        // 기존 PNU 업데이트
        existingPnu.setSidoName(req.getSido());
        existingPnu.setSigunguName(req.getSigungu());
        existingPnu.setBjdName(req.getBjd());
        toUpdate.add(existingPnu);
      } else {
        // 새 PNU 생성
        PnuTable newPnu = createNewPnuTable(req);
        toInsert.add(newPnu);
      }
    }

    // 4. 배치로 저장 (각각 한 번의 쿼리로)
    if (!toUpdate.isEmpty()) {
      pnuTableRepository.saveAll(toUpdate); // 배치 UPDATE
    }

    if (!toInsert.isEmpty()) {
      pnuTableRepository.saveAll(toInsert); // 배치 INSERT
    }
  }

  private PnuTable createNewPnuTable(PnuCreateReq req) {
    PnuTableType type;
    String pnu = req.getPnu();
    String sidoCode = pnu.substring(0, 2);
    String sigunguCode = null;
    String bjdCode = null;

    if (req.getSigungu() == null && req.getBjd() == null) {
      type = PnuTableType.SIDO;
    } else if (req.getSigungu() != null && req.getBjd() == null) {
      type = PnuTableType.SIGUNGU;
      sigunguCode = pnu.substring(2, 5);
    } else {
      sigunguCode = pnu.substring(2, 5);
      bjdCode = pnu.substring(5, 10);
      type = PnuTableType.BJDONG;
    }

    return PnuTable.builder()
        .pnu(req.getPnu())
        .type(type)
        .sido(sidoCode)
        .sidoName(req.getSido())
        .sigungu(sigunguCode)
        .sigunguName(req.getSigungu())
        .bjd(bjdCode)
        .bjdName(req.getBjd())
        .build();
  }

  public Optional<PnuTable> getByPnu(String pnu) {
    return pnuTableRepository.findByPnu(pnu);
  }

  public String getSidoName(String sidoCode) {

    if (sidoCode == null) {
      return null;
    }

    Optional<PnuTable> optionalPnu = pnuTableRepository.findBySidoCode(sidoCode, PnuTableType.SIDO);
    if (optionalPnu.isEmpty()) {
      return null;
    } else {
      PnuTable pnuTable = optionalPnu.get();
      return pnuTable.getSidoName();
    }
  }

  public String getSigunguName(String sidoCode, String sigunguCode) {
    if (sigunguCode == null || sidoCode == null) {
      return null;
    }
    Optional<PnuTable> optionalPnu = pnuTableRepository.findBySigunguCode(sidoCode, sigunguCode,
        PnuTableType.SIGUNGU);
    if (optionalPnu.isEmpty()) {
      return null;
    } else {
      PnuTable pnuTable = optionalPnu.get();
      return pnuTable.getSigunguName();
    }
  }

  public String getBjdongName(String sidoCode, String sigunguCode, String bjdongCode) {
    if (bjdongCode == null || sigunguCode == null || sidoCode == null) {
      return null;
    }
    Optional<PnuTable> optionalPnu = pnuTableRepository.findByBjdCode(sidoCode, sigunguCode, bjdongCode,
        PnuTableType.BJDONG);
    if (optionalPnu.isEmpty()) {
      return null;
    } else {
      PnuTable pnuTable = optionalPnu.get();
      return pnuTable.getBjdName();
    }
  }

  public List<PnuTable> getsSido() {
    return pnuTableRepository.findAllByType(PnuTableType.SIDO);
  }

  public List<PnuTable> getsSigungu(String sidoCode) {
    return pnuTableRepository.findAllByTypeAndSido(PnuTableType.SIGUNGU, sidoCode);
  }

  public List<PnuTable> getsBjd(String sidoCode, String sigunguCode) {
    return pnuTableRepository.findAllByTypeAndSidoAndSigungu(PnuTableType.BJDONG, sidoCode,
        sigunguCode);
  }

  public PnuResp pnuResp(PnuTable pnuTable) {

    String name;
    String code;

    switch (pnuTable.getType()) {
      case SIDO:
        name = pnuTable.getSidoName();
        code = pnuTable.getSido();
        break;
      case SIGUNGU:
        name = pnuTable.getSigunguName();
        code = pnuTable.getSigungu();
        break;
      case BJDONG:
        name = pnuTable.getBjdName();
        code = pnuTable.getBjd();
        break;
      default:
        throw new IllegalStateException("올바르지 않은 pnu입니다");
    }

    return PnuResp.builder()
        .pnu(pnuTable.getPnu())
        .name(name)
        .code(code)
        .build();
  }

  public List<PnuTable> getByPnuIn(List<String> pnuList) {
    return pnuTableRepository.findAllByPnuIn(pnuList);
  }

  @Transactional
  public void deleteAll(List<PnuTable> existingPnus) {
    pnuTableRepository.deleteAll(existingPnus);
  }
}
