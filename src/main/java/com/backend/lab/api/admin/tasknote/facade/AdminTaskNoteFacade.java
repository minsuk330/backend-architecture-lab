package com.backend.lab.api.admin.tasknote.facade;

import com.backend.lab.api.admin.tasknote.dto.TaskNoteOptions;
import com.backend.lab.common.entity.dto.resp.ListResp;
import com.backend.lab.common.entity.dto.resp.PageResp;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.admin.core.service.AdminService;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.core.service.PropertyService;
import com.backend.lab.domain.property.taskNote.entity.TaskNote;
import com.backend.lab.domain.property.taskNote.entity.dto.req.SearchTaskNoteOptions;
import com.backend.lab.domain.property.taskNote.entity.dto.req.TaskNoteCreateReq;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteResp;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteSearchResp;
import com.backend.lab.domain.property.taskNote.service.TaskNoteService;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminTaskNoteFacade {
  private final TaskNoteService taskNoteService;
  private final PropertyService propertyService;
  private final AdminService adminService;
  private final UploadFileService uploadFileService;

  public PageResp<TaskNoteSearchResp> search(SearchTaskNoteOptions options) {
    Page<TaskNoteSearchResp> page = taskNoteService.search(options);

    List<TaskNoteSearchResp> list = page.getContent().stream()
        .toList();
    return new PageResp<>(page, list);
  }


  @Transactional
  public void create(TaskNoteCreateReq req, Long adminId, Long propertyId) {

    Admin admin = adminService.getById(adminId);
    Property property = propertyService.getById(propertyId);
    if (req.getImageId()!=null) {
      req.setImage(uploadFileService.getById(req.getImageId()));
    }
    taskNoteService.updateCreate(req,admin,property);
  }

  public ListResp<TaskNoteResp> getByProperty(Long propertyId) {
    List<TaskNote> tasknotes = taskNoteService.getByProperty(propertyId);

    List<TaskNoteResp> list = tasknotes.stream().map(taskNoteService::taskNoteResp).toList();
    return new ListResp<>(list);
  }

  public PageResp<TaskNoteResp> getByProperty(TaskNoteOptions options) {
    Page<TaskNote> page = taskNoteService.getByProperty(options);

    List<TaskNoteResp> list = page.getContent().stream().map(
        taskNoteService::taskNoteResp
    ).toList();

    return new PageResp<>(page, list);
  }

  public ListResp<TaskNoteResp> gets() {
    List<TaskNote> list = taskNoteService.getRecent();
    List<TaskNoteResp> data = list.stream().map(
        taskNoteService::taskNoteResp
    ).toList();

    return new ListResp<>(data);

  }

  @Transactional
  public void delete(Long taskNoteId) {
    taskNoteService.delete(taskNoteId);
  }
}
