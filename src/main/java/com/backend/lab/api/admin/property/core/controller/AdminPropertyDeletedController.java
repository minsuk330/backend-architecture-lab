package com.backend.lab.api.admin.property.core.controller;

import static com.backend.lab.common.util.AuthUtil.*;

import com.backend.lab.api.admin.property.core.dto.resp.PropertyResp;
import com.backend.lab.api.admin.property.core.facade.AdminPropertyFacade;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.util.AuthUtil;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequireAdminRole
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/property/deleted")
@Tag(name = "[관리자] 삭제된 매물")
public class AdminPropertyDeletedController {

  private final AdminPropertyFacade adminPropertyFacade;

  @Operation(summary = "매물 목록 조회")
  @GetMapping
  public PageResp<PropertySearchResp> search(
      @RequestParam(required = false) List<Long> adminIds,
      @RequestParam(required = false) List<Long> bigCategoryIds,
      @RequestParam(required = false) List<Long> smallCategoryIds,
      @ModelAttribute SearchPropertyOptions options
  ) {
    return adminPropertyFacade.searchDeleted(options,adminIds,bigCategoryIds,smallCategoryIds);
  }

  @Operation(summary = "매물 상세 조회")
  @GetMapping("/{propertyId}")
  public PropertyResp geyById(
      @PathVariable("propertyId") Long propertyId
  ) {
    return adminPropertyFacade.getDeletedById(propertyId, getUserId());
  }

  @Operation(
      summary = "삭제된 엑셀 다운로드",
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
    ByteArrayInputStream excelStream = adminPropertyFacade.createDeletedExcelFile(AuthUtil.getUserId(), ids);
    String filename = "properties_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

    InputStreamResource resource = new InputStreamResource(excelStream);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
        .body(resource);
  }



  @Operation(summary = "매물 복구")
  @GetMapping("/restore/{id}")
  public void restore(
      @PathVariable("id") Long id
  ) {
    adminPropertyFacade.restore(id);
  }

  @Operation(summary = "매물 영구 삭제")
  @DeleteMapping("/remove/{id}")
  public void remove(
      @PathVariable("id") Long id
  ) {
    adminPropertyFacade.remove(id);
  }
}
