package com.backend.lab.api.admin.property.core.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;
import static com.backend.lab.common.util.NetworkUtil.getClientIp;

import com.backend.lab.api.admin.property.core.dto.req.PropertyAdvertisementCreateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyAdvertisementRemoveReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyCreateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyMapListReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyStatusUpdateReq;
import com.backend.lab.api.admin.property.core.dto.req.PropertyUpdateReq;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyDetailResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyPublicResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyStatusResp;
import com.backend.lab.api.admin.property.core.dto.resp.PropertyUpdateResp;
import com.backend.lab.api.admin.property.core.facade.AdminPropertyFacade;
import com.backend.lab.api.common.searchFilter.dto.req.SearchPropertyOptions;
import com.backend.lab.api.member.common.property.wishlist.dto.PropertyListResp;
import com.backend.lab.common.auth.annotation.RequireAdminRole;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.common.util.AuthUtil;
import com.backend.lab.domain.property.core.entity.dto.resp.PropertySearchResp;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/property")
@Tag(name = "[관리자] 매물 설정")
public class AdminPropertyController {

  private final AdminPropertyFacade adminPropertyFacade;

  @Operation(summary = "매물 목록 조회")
  @GetMapping("/list")
  public PageResp<PropertySearchResp> search(
      @RequestParam(required = false) List<Long> adminIds,
      @RequestParam(required = false) List<Long> bigCategoryIds,
      @RequestParam(required = false) List<Long> smallCategoryIds,
      @ModelAttribute SearchPropertyOptions options
  ) {
    return adminPropertyFacade.search(options, adminIds, bigCategoryIds, smallCategoryIds);
  }

  @Operation(summary = "CSV 다운로드 (고속)")
  @GetMapping("/csv-download")
  public ResponseEntity<Resource> csvDownload(
      @RequestParam(required = false) List<Long> ids,
      HttpServletResponse response
  ) {
    ByteArrayInputStream csvStream = adminPropertyFacade.createFastCsvFile(AuthUtil.getUserId(),
        ids);
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
    ByteArrayInputStream excelStream = adminPropertyFacade.createExcelFile(AuthUtil.getUserId(), ids);
    String filename = "properties_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

    InputStreamResource resource = new InputStreamResource(excelStream);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
        .body(resource);
  }

  @Operation(summary = "엑셀 업로드")
  @PostMapping(path = "/excel-upload",consumes = "multipart/form-data", produces = "application/json")
  public void excelUpload(
      @RequestParam("file") MultipartFile file
  ) {
    adminPropertyFacade.uploadExcelFile(file);
  }

  @Operation(summary = "업무일지 업로드")
  @PostMapping(path = "/tasknote-upload",consumes = "multipart/form-data", produces = "application/json")
  public void taskNoteUpload(
      @RequestParam("file") MultipartFile file
  ) {
    adminPropertyFacade.taskNoteUploadFile(file);
  }


  @Operation(summary = "매물 리스트 조회")
  @GetMapping("home/list")
  public ListResp<PropertyListResp> getList() {
    return adminPropertyFacade.getList();
  }

  @Operation(summary = "매물 지도 조회")
  @GetMapping("list/map")
  public ListResp<PropertyListResp> getByMap(
      @RequestParam(required = false) List<Long> adminIds,
      @RequestParam(required = false) List<Long> bigCategoryIds,
      @RequestParam(required = false) List<Long> smallCategoryIds,
      @ModelAttribute PropertyMapListReq req
  ) {
    return adminPropertyFacade.getByMap(req, adminIds, bigCategoryIds, smallCategoryIds);
  }

  @Hidden
  @Operation(summary = "층별정보 업데이트")
  @GetMapping("/floor-update")
  public void floorUpdate() {
    adminPropertyFacade.floorUpdate();
  }

  @Operation(summary = "매물 상세 조회")
  @GetMapping("/detail/{propertyId}")
  public PropertyResp get(
      @PathVariable("propertyId") Long propertyId
  ) {
    return adminPropertyFacade.get(propertyId, getUserId());
  }

  //매물 생성
  @Operation(summary = "매물 생성")
  @PostMapping
  public void create(
      @RequestBody PropertyCreateReq req,
      HttpServletRequest request
  ) {
    adminPropertyFacade.createProperty(req, getUserId(), getClientIp(request));
  }

  @Operation(summary = "매물 수정 조회")
  @GetMapping("/{propertyId}")
  public PropertyUpdateResp update(
      @PathVariable("propertyId") Long propertyId
  ) {
    return adminPropertyFacade.getUpdateProperty(AuthUtil.getUserId(), propertyId);
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 수정")
  @PutMapping("/{propertyId}")
  //매도자, 업무일지 삭제 안됨
  public void update(
      @RequestBody PropertyUpdateReq req,
      @PathVariable("propertyId") Long propertyId,
      HttpServletRequest request

  ) {
    adminPropertyFacade.updateProperty(req, getUserId(), propertyId, getClientIp(request));
  }

  @Operation(summary = "매물 통계")
  @GetMapping("home/stat")
  public PropertyStatResp getStatistics(
      @RequestParam(required = false) Long totalDepartmentId,
      @RequestParam(required = false) Long totalMajorCategoryId,
      @RequestParam(required = false) Long totalAdminId,
      @RequestParam(required = false) Long monthlyDepartmentId,
      @RequestParam(required = false) Long monthlyMajorCategoryId,
      @RequestParam(required = false) Long monthlyAdminId
      ) {
    return adminPropertyFacade.getStat(totalDepartmentId,totalMajorCategoryId,totalAdminId,monthlyAdminId,monthlyDepartmentId,monthlyMajorCategoryId);
  }

  @Operation(summary = "매물 개요")
  @GetMapping("/{propertyId}/info")
  public PropertyDetailResp getInfo(
      @PathVariable("propertyId") Long propertyId
  ) {
    return adminPropertyFacade.getInfo(propertyId, getUserId());
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 공개여부 토글")
  @PatchMapping("/{propertyId}/public")
  public PropertyPublicResp isPublicToggle(
      @PathVariable("propertyId") Long propertyId
  ) {
    return adminPropertyFacade.updatePublicStatus(propertyId);
  }

  @Operation(summary = "매물 상태조회")
  @GetMapping("{propertyId}/status")
  public PropertyStatusResp updatePropertyStatus(
      @PathVariable("propertyId") Long propertyId
  ) {
    return adminPropertyFacade.getStatus(propertyId);
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 상태변경")
  @PutMapping("{propertyId}/status")
  public PropertyStatusResp updatePropertyStatus(
      @PathVariable("propertyId") Long propertyId,
      @RequestBody PropertyStatusUpdateReq req
  ) {
    return adminPropertyFacade.statusUpdate(req, propertyId, getUserId());
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 삭제")
  @DeleteMapping("/{propertyId}")
  public void delete(
      @PathVariable("propertyId") Long propertyId,
      HttpServletRequest request
  ) {
    adminPropertyFacade.delete(propertyId, getUserId(),getClientIp(request));
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 확인일 설정/갱신")
  @PatchMapping("/{propertyId}/confirm")
  public void confirm (
      @PathVariable("propertyId") Long propertyId
  ) {
    adminPropertyFacade.confirm(propertyId);
  }

  @PreAuthorize("hasAuthority('MANAGE_MAEMOOL')")
  @Operation(summary = "매물 확인일 삭제")
  @DeleteMapping("/{propertyId}/confirm")
  public void removeConfirm (
      @PathVariable("propertyId") Long propertyId
  ) {
    adminPropertyFacade.removeConfirm(propertyId);
  }

  @RequireAdminRole
  @Operation(summary = "매물에 공인중개사 광고 추가")
  @PostMapping("/advertisement")
  public void assignPropertyAdvertisement(
      @Valid @RequestBody PropertyAdvertisementCreateReq req
  ) {
    adminPropertyFacade.assignPropertyAdvertisement(
        req.getPropertyId(),
        req.getAgentId(),
        req.getStartDate(),
        req.getEndDate()
    );
  }

  @RequireAdminRole
  @Operation(summary = "매물에서 공인중개사 광고 삭제")
  @DeleteMapping("/advertisement")
  public void removePropertyAdvertisement(
      @Valid @RequestBody PropertyAdvertisementRemoveReq req
  ) {
    adminPropertyFacade.removePropertyAdvertisement(
        req.getPropertyId(),
        req.getAgentId()
    );
  }

}
