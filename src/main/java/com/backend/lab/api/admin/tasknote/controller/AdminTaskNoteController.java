package com.backend.lab.api.admin.tasknote.controller;

import static com.backend.lab.common.util.AuthUtil.getUserId;

import com.backend.lab.api.admin.tasknote.dto.TaskNoteOptions;
import com.backend.lab.api.admin.tasknote.facade.AdminTaskNoteFacade;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.property.taskNote.entity.dto.req.SearchTaskNoteOptions;
import com.backend.lab.domain.property.taskNote.entity.dto.req.TaskNoteCreateReq;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteResp;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteSearchResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAuthority('MANAGE_TASK_AND_CUSTOMER_HISTORY')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/note")
@Tag(name = "[관리자] 업무일지 조회")
public class AdminTaskNoteController {

  private final AdminTaskNoteFacade adminTaskNoteFacade;

  @Operation(summary = "매물 업무일지 조회")
  @GetMapping("/{propertyId}")
  public ListResp<TaskNoteResp> get(
      @PathVariable("propertyId") Long propertyId
  ) {
    return adminTaskNoteFacade.getByProperty(propertyId);
  }

  @Operation(summary = "업무일지 모달")
  @GetMapping("/modal")
  public PageResp<TaskNoteResp> get(
      @ModelAttribute TaskNoteOptions options
  ) {
    return adminTaskNoteFacade.getByProperty(options);
  }

  @Operation(summary = "매매 업무일지")
  @GetMapping("/list")
  public ListResp<TaskNoteResp> get() {
    return adminTaskNoteFacade.gets();
  }

  @Operation(summary = "업무일지 검색")
  @GetMapping
  public PageResp<TaskNoteSearchResp> search(
      @ModelAttribute SearchTaskNoteOptions options
  ) {
    return adminTaskNoteFacade.search(options);
  }

  @Operation(summary = "업무일지 추가")
  @PostMapping("/{propertyId}/register")
  public void create(
      @RequestBody TaskNoteCreateReq req,
      @PathVariable("propertyId") Long propertyId
  ) {
    adminTaskNoteFacade.create(req, getUserId(), propertyId);
  }

  @Operation(summary = "업무일지 삭제")
  @DeleteMapping("/{taskNoteId}")
  public void delete(
      @PathVariable("taskNoteId") Long taskNoteId
  ) {
    adminTaskNoteFacade.delete(taskNoteId);
  }
}
