package com.backend.lab.application.service.property.file;

import com.backend.lab.application.port.in.property.file.DownloadCsvUseCase;
import com.backend.lab.common.exception.BusinessException;
import com.backend.lab.common.exception.ErrorCode;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.entity.embedded.AddressProperties;
import com.backend.lab.domain.property.core.entity.embedded.PriceProperties;
import com.backend.lab.domain.property.core.repository.PropertyRepository;
import com.backend.lab.domain.property.core.service.PropertyService;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DownloadCsvService implements DownloadCsvUseCase {

    private final PropertyService propertyService;
    private final PropertyRepository propertyRepository;

    @Override
    public ByteArrayInputStream createFastCsvFile(Long adminId, List<Long> ids) {
        List<Property> properties;
        if (ids != null && !ids.isEmpty()) {
            properties = propertyRepository.findByIdInWithAllDetails(ids).stream()
                .sorted(Comparator.comparing(Property::getId))
                .toList();
        } else {
            properties = getAllPropertiesWithPaging();
        }

        final String[] HEADERS = {
            "매물번호", "공개여부", "진행상태", "건물명", "대분류", "거래유형",
            "등록일", "수정일", "상세주소", "매매가", "보증금", "월임대료"
        };

        try {
            StringBuilder csv = new StringBuilder();
            csv.append(String.join(",", HEADERS)).append("\n");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Property property : properties) {
                List<String> row = new ArrayList<>();

                row.add(csvValue(property.getId()));
                row.add(csvValue(property.getIsPublic() != null ? (property.getIsPublic() ? "공개" : "비공개") : ""));
                row.add(csvValue(property.getStatus() != null ? property.getStatus().getDisplayName() : ""));
                row.add(csvValue(property.getBuildingName()));
                row.add(csvValue(property.getBigCategory() != null ? property.getBigCategory().getName() : ""));
                row.add(csvValue("매매"));
                row.add(csvValue(property.getCreatedAt() != null ? formatter.format(property.getCreatedAt()) : ""));
                row.add(csvValue(property.getUpdatedAt() != null ? formatter.format(property.getUpdatedAt()) : ""));

                try {
                    AddressProperties address = property.getAddress() != null ? property.getAddress().getProperties() : null;
                    row.add(csvValue(address != null ? address.getRoadAddress() : ""));
                } catch (Exception e) {
                    row.add("");
                }

                try {
                    PriceProperties price = property.getPrice() != null ? property.getPrice().getProperties() : null;
                    row.add(csvValue(price != null ? price.getMmPrice() : ""));
                    row.add(csvValue(price != null ? price.getDepositPrice() : ""));
                    row.add(csvValue(price != null ? price.getMonthPrice() : ""));
                } catch (Exception e) {
                    row.add("");
                    row.add("");
                    row.add("");
                }

                csv.append(String.join(",", row)).append("\n");
            }

            return new ByteArrayInputStream(csv.toString().getBytes("UTF-8"));

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ERROR_WHITE_PROCESS_XLSX, e.getMessage());
        }
    }

    private List<Property> getAllPropertiesWithPaging() {
        long startTime = System.currentTimeMillis();
        List<Property> allProperties = new ArrayList<>();
        int batchSize = 1000;
        int page = 0;

        log.info("Starting paging query with batch size: {}", batchSize);

        while (true) {
            Pageable pageable = PageRequest.of(page, batchSize);
            Page<Property> propertyPage = propertyRepository.findAllWithBasicDetails(pageable);

            allProperties.addAll(propertyPage.getContent());

            if (!propertyPage.hasNext()) {
                break;
            }
            page++;
        }

        long totalTime = System.currentTimeMillis() - startTime;
        log.info("Paging query completed: Total {}ms, Records: {}", totalTime, allProperties.size());

        return allProperties;
    }

    private String csvValue(Object value) {
        if (value == null) return "";
        String str = String.valueOf(value);
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            str = str.replace("\"", "\"\"");
            return "\"" + str + "\"";
        }
        return str;
    }
}
