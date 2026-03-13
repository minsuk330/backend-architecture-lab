package com.backend.lab.api.admin.property.core.controller;

import com.backend.lab.application.port.in.property.file.DownloadCsvUseCase;
import com.backend.lab.application.port.in.property.file.DownloadExcelUseCase;
import com.backend.lab.application.port.in.property.file.UploadExcelUseCase;
import com.backend.lab.application.port.in.property.file.UploadTaskNoteUseCase;
import com.backend.lab.common.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/property")
@Tag(name = "[관리자] 매물 파일 업로드")
public class AdminPropertyFileController {

  private final DownloadCsvUseCase downloadCsvUseCase;
  private final DownloadExcelUseCase downloadExcelUseCase;
  private final UploadExcelUseCase uploadExcelUseCase;
  private final UploadTaskNoteUseCase uploadTaskNoteUseCase;

  @Operation(summary = "CSV 다운로드 (고속)")
  @GetMapping("/csv-download")
  public ResponseEntity<Resource> csvDownload(
      @RequestParam(required = false) List<Long> ids,
      HttpServletResponse response
  ) {
    ByteArrayInputStream csvStream = downloadCsvUseCase.createFastCsvFile(AuthUtil.getUserId(), ids);
    String filename = "properties_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HH_mm_ss")) + ".csv";

    InputStreamResource resource = new InputStreamResource(csvStream);

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
        .header("Content-Disposition", "attachment; filename=\"%s\"".formatted(filename))
        .body(resource);
  }

  @Operation(
      summary = "엑셀 다운로드",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "엑셀 파일 다운로드 성공",
              content = @Content(
                  mediaType = "application/octet-stream",
                  schema = @Schema(type = "string", format = "binary")
              )
          )
      }
  )
  @GetMapping("/excel-download")
  public ResponseEntity<Resource> excelDownload(
      @RequestParam(required = false) List<Long> ids,
      HttpServletResponse response
  ) {
    ByteArrayInputStream excelStream = downloadExcelUseCase.createExcelFile(AuthUtil.getUserId(), ids);
    String filename = "properties_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

    InputStreamResource resource = new InputStreamResource(excelStream);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
        .body(resource);
  }

  @Operation(summary = "엑셀 업로드")
  @PostMapping(path = "/excel-upload", consumes = "multipart/form-data", produces = "application/json")
  public void excelUpload(
      @RequestParam("file") MultipartFile file
  ) {
    uploadExcelUseCase.uploadExcelFile(file);
  }

  @Operation(summary = "업무일지 업로드")
  @PostMapping(path = "/tasknote-upload", consumes = "multipart/form-data", produces = "application/json")
  public void taskNoteUpload(
      @RequestParam("file") MultipartFile file
  ) {
    uploadTaskNoteUseCase.taskNoteUploadFile(file);
  }


}
