package com.backend.lab.api.admin.dbUpdate.facade;

import com.backend.lab.common.entity.dto.req.PageOptions;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.property.conflictProperty.service.ConflictPropertyService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.pnuTable.entity.PnuTable;
import com.backend.lab.domain.property.pnuTable.entity.PnuTableHistory;
import com.backend.lab.domain.property.pnuTable.entity.dto.req.PnuCreateReq;
import com.backend.lab.domain.property.pnuTable.service.PnuTableHistoryService;
import com.backend.lab.domain.property.pnuTable.service.PnuTableService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPnuFacade {

  private final PnuTableService pnuTableService;
  private final PnuTableHistoryService pnuTableHistoryService;
  private final PropertyService propertyService;
  private final ConflictPropertyService conflictPropertyService;

  private static final int BATCH_SIZE = 1000;

  public PageResp<PnuTableHistory> getsHistory(PageOptions pageOptions) {
    Page<PnuTableHistory> page = pnuTableHistoryService.gets(pageOptions.pageable());
    return new PageResp<>(page, page.getContent());
  }

  @Transactional
  public void process(MultipartFile file) {

    if (conflictPropertyService.existsAny()) {
      throw new BusinessException(ErrorCode.CANT_PROCESS_WITH_CONFLICT);
    }

    try (InputStream fis = file.getInputStream()) {
      InputStreamReader isReader = new InputStreamReader(fis, "euc-kr");
      CSVReader csvReader = new CSVReader(isReader);

      List<PnuCreateReq> batchInsertList = new ArrayList<>();
      List<String> pnuCheckList = new ArrayList<>();

      String[] record;
      boolean isHeader = true;

      while ((record = csvReader.readNext()) != null) {
        if (isHeader) {
          String[] headerRows = Arrays.stream(record)
              .flatMap(str -> Arrays.stream(str.split("\t")))
              .toList().toArray(new String[0]);

          if (headerRows.length != 3) {
            throw new BusinessException(ErrorCode.PNU_INVALID_FILE);
          }

          final String[] requireHeaders = {"법정동코드", "법정동명", "폐지여부"};

          for (String requiredHeader : requireHeaders) {
            if (!Arrays.asList(headerRows).contains(requiredHeader)) {
              throw new BusinessException(ErrorCode.PNU_INVALID_FILE);
            }
          }

          isHeader = false;
          continue;
        }

        String[] processedRecord = Arrays.stream(record)
            .flatMap(str -> Arrays.stream(str.split("\t")))
            .toList().toArray(new String[0]);

        String pnu = processedRecord[0];
        boolean isExist = processedRecord[processedRecord.length - 1].equals("존재");

        if (!isExist) {
          pnuCheckList.add(pnu);
        } else {
          PnuCreateReq req = buildPnuCreateReq(pnu, processedRecord);
          if (req != null) {
            batchInsertList.add(req);
          }
        }

        if (batchInsertList.size() >= BATCH_SIZE) {
          pnuTableService.saveAll(batchInsertList);
          batchInsertList.clear();
        }

        if (pnuCheckList.size() >= BATCH_SIZE) {
          processMissingPnusBatch(pnuCheckList);
          pnuCheckList.clear();
        }
      }

      if (!batchInsertList.isEmpty()) {
        pnuTableService.saveAll(batchInsertList);
      }

      if (!pnuCheckList.isEmpty()) {
        processMissingPnusBatch(pnuCheckList);
      }

      pnuTableHistoryService.create(file.getName());

    } catch (IOException | CsvException e) {
      throw new RuntimeException(e);
    }
  }

  private PnuCreateReq buildPnuCreateReq(String pnu, String[] record) {

    String[] addressName = record[1].split(" ");

    int sidoCode = Integer.parseInt(pnu.substring(0, 2));
    int sigunguCode = Integer.parseInt(pnu.substring(2, 5));
    int bjdCode = Integer.parseInt(pnu.substring(5, 8));

    String sido = null;
    String sigungu = null;
    String bjd = null;
    int length = addressName.length;

    if (sidoCode == 0) {
      return null;
    }

    // case : sido
    if (sigunguCode == 0) {
      StringBuilder sb = new StringBuilder();
      for (String s : addressName) {
        sb.append(s).append(' ');
      }
      sido = sb.toString().trim();
      return PnuCreateReq.builder()
          .pnu(pnu)
          .sido(sido)
          .sigungu(sigungu)
          .bjd(bjd)
          .build();
    }

    // case : sigungu
    if (bjdCode == 0) {
      sido = addressName[0].trim();
      StringBuilder sb = new StringBuilder();
      for (int i = 1; i < addressName.length; i++) {
        sb.append(addressName[i]).append(' ');
      }
      sigungu = sb.toString().trim();
      return PnuCreateReq.builder()
          .pnu(pnu)
          .sido(sido)
          .sigungu(sigungu)
          .bjd(bjd)
          .build();
    }

    sido = addressName[0].trim();
    final String[] sigunguPostfix = {"시", "군", "구"};

    int i = 1;
    StringBuilder sigunguSb = new StringBuilder();
    while (i < addressName.length) {
      boolean matched = false;

      for (String postFix : sigunguPostfix) {
        if (addressName[i].endsWith(postFix)) {
          sigunguSb.append(addressName[i]).append(' ');
          matched = true;
          break;
        }
      }

      if (!matched) {
        break;
      }

      i++;
    }
    sigungu = sigunguSb.toString().trim();

    StringBuilder bjdSb = new StringBuilder();
    while (i < addressName.length) {
      bjdSb.append(addressName[i++]).append(' ');
    }
    bjd = bjdSb.toString().trim();

    return PnuCreateReq.builder()
        .pnu(pnu)
        .sido(sido.trim())
        .sigungu(sigungu)
        .bjd(bjd)
        .build();
  }

  private void processMissingPnusBatch(List<String> pnuList) {
    try {
      // 한 번에 여러 PNU 존재 여부 확인
      List<PnuTable> existingPnus = pnuTableService.getByPnuIn(pnuList);
      Set<String> existingPnuSet = existingPnus.stream()
          .map(PnuTable::getPnu)
          .collect(Collectors.toSet());

      List<Property> allConflicts = new ArrayList<>();

      for (String pnu : pnuList) {
        if (existingPnuSet.contains(pnu)) {
          List<Property> conflicts = propertyService.getsByPnuStartWith(pnu);
          allConflicts.addAll(conflicts);
        }
      }

      if (!allConflicts.isEmpty()) {
        conflictPropertyService.create(allConflicts);
      }

      pnuTableService.deleteAll(existingPnus);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}