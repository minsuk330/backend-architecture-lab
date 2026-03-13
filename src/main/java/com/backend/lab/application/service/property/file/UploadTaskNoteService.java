package com.backend.lab.application.service.property.file;

import com.backend.lab.application.port.in.property.file.UploadTaskNoteUseCase;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.propertyWorkLog.entity.vo.LogFieldType;
import com.backend.lab.domain.property.taskNote.entity.vo.TaskType;
import com.backend.lab.domain.property.taskNote.repository.TaskNoteRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadTaskNoteService implements UploadTaskNoteUseCase {

    private static final int BATCH_SIZE = 1000;

    private final TaskNoteRepository taskNoteRepository;
    private final PropertyService propertyService;

    @Override
    public void taskNoteUploadFile(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            List<TaskNoteInsertData> batchInsertList = new ArrayList<>();
            int processedCount = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                try {
                    TaskNoteInsertData insertData = parseRowToTaskNoteData(row);
                    if (insertData != null) {
                        batchInsertList.add(insertData);
                        processedCount++;
                    }
                } catch (Exception e) {
                    log.error("행 {} 처리 실패: {}", row.getRowNum(), e.getMessage());
                    continue;
                }

                if (batchInsertList.size() >= BATCH_SIZE) {
                    processBatchInsertWithTransaction(batchInsertList);
                    log.info("배치 처리 완료: {} 건", batchInsertList.size());
                    batchInsertList.clear();
                }
            }

            if (!batchInsertList.isEmpty()) {
                processBatchInsertWithTransaction(batchInsertList);
                log.info("최종 배치 처리 완료: {} 건", batchInsertList.size());
            }

            log.info("TaskNote 업로드 완료 - 총 처리: {} 건", processedCount);

        } catch (Exception e) {
            log.error("엑셀 업로드 실패", e);
            throw new RuntimeException("업로드 실패: " + e.getMessage());
        }
    }

    @Transactional
    public void processBatchInsertWithTransaction(List<TaskNoteInsertData> batchList) {
        processBatchInsert(batchList);
    }

    private void processBatchInsert(List<TaskNoteInsertData> batchList) {
        for (TaskNoteInsertData data : batchList) {
            try {
                if (data.taskNoteData != null) {
                    taskNoteRepository.insertTaskNoteAuto(
                        data.propertyId,
                        data.taskType.name(),
                        data.createdAt,
                        data.taskNoteData.beforeValue,
                        data.taskNoteData.afterValue,
                        data.taskNoteData.logFieldType.ordinal(),
                        data.adminId
                    );
                } else {
                    taskNoteRepository.insertTaskNote(
                        data.propertyId,
                        data.taskType.name(),
                        data.createdAt,
                        data.content,
                        data.adminId
                    );
                }
            } catch (Exception e) {
                log.error("TaskNote 저장 실패 - propertyId: {}, error: {}", data.propertyId, e.getMessage());
            }
        }
    }

    private TaskNoteInsertData parseRowToTaskNoteData(Row row) {
        String propertyIdStr = getCellValue(row, 1);
        String type = getCellValue(row, 5);
        String content = getCellValue(row, 6);
        String createdAtStr = getCellValue(row, 7);

        LocalDateTime createdAt = parseDateTime(createdAtStr);

        if (propertyIdStr.isEmpty()) {
            log.warn("필수 정보 누락으로 행 건너뜀 - 매물번호: {}", propertyIdStr);
            return null;
        }

        Long propertyId = Long.parseLong(propertyIdStr);

        if (!propertyService.getByexist(propertyId)) {
            log.warn("존재하지 않는 매물번호로 행 건너뜀 - 매물번호: {}", propertyId);
            return null;
        }

        TaskType taskType = convertToTaskType(type);
        Long adminId = 2L;

        if (taskType == TaskType.AUTO_UPDATE) {
            TaskNoteData taskNoteData = parseTaskNoteContent(content);
            return new TaskNoteInsertData(propertyId, taskType, createdAt, content, adminId, taskNoteData);
        }

        return new TaskNoteInsertData(propertyId, taskType, createdAt, content, adminId, null);
    }

    private TaskType convertToTaskType(String statusStr) {
        if (statusStr == null || statusStr.isEmpty()) {
            return null;
        }
        switch (statusStr) {
            case "미팅": return TaskType.MEETING;
            case "변경": return TaskType.AUTO_UPDATE;
            case "계약": return TaskType.CONTRACT;
            case "기타": return TaskType.ETC;
            case "임장": return TaskType.IMJANG;
            case "전화": return TaskType.CALL;
            default:
                log.warn("알 수 없는 상태값: {}", statusStr);
                return null;
        }
    }

    public TaskNoteData parseTaskNoteContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        String pattern = "^(.+?)\\s*:\\s*(.+?)\\s*▶\\s*(.+?)\\s*으로";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(content.trim());

        if (matcher.find()) {
            String koreanFieldType = matcher.group(1).trim();
            String beforeValue = matcher.group(2).trim();
            String afterValue = matcher.group(3).trim();

            LogFieldType logFieldType = mapKoreanToLogFieldType(koreanFieldType);
            if (logFieldType != null) {
                return new TaskNoteData(logFieldType, beforeValue, afterValue);
            }
        }
        return null;
    }

    private LogFieldType mapKoreanToLogFieldType(String korean) {
        return switch (korean) {
            case "고객휴대전화" -> LogFieldType.PHONE_NUMBER;
            case "진행상태" -> LogFieldType.STATUS;
            case "수익률" -> LogFieldType.ROI;
            case "담당자" -> LogFieldType.ADMIN;
            case "월임대료" -> LogFieldType.MONTH_PRICE;
            case "보증금" -> LogFieldType.DEPOSIT_PRICE;
            case "평단가" -> LogFieldType.PYENG_PRICE;
            case "매매가" -> LogFieldType.MM_PRICE;
            default -> null;
        };
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty() || dateTimeStr.equals("-")) {
            return LocalDateTime.now();
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}, 현재 시간으로 대체", dateTimeStr);
            return LocalDateTime.now();
        }
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numValue = cell.getNumericCellValue();
                    if (numValue == Math.floor(numValue)) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    private static class TaskNoteInsertData {
        public final Long propertyId;
        public final TaskType taskType;
        public final LocalDateTime createdAt;
        public final String content;
        public final Long adminId;
        public final TaskNoteData taskNoteData;

        public TaskNoteInsertData(Long propertyId, TaskType taskType, LocalDateTime createdAt,
                                  String content, Long adminId, TaskNoteData taskNoteData) {
            this.propertyId = propertyId;
            this.taskType = taskType;
            this.createdAt = createdAt;
            this.content = content;
            this.adminId = adminId;
            this.taskNoteData = taskNoteData;
        }
    }

    public static class TaskNoteData {
        public final LogFieldType logFieldType;
        public final String beforeValue;
        public final String afterValue;

        public TaskNoteData(LogFieldType logFieldType, String beforeValue, String afterValue) {
            this.logFieldType = logFieldType;
            this.beforeValue = beforeValue;
            this.afterValue = afterValue;
        }
    }
}
