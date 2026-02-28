package com.backend.lab.domain.property.taskNote.service;

import com.backend.lab.api.admin.property.core.dto.req.PropertyTaskNoteUpdateReq;
import com.backend.lab.api.admin.tasknote.dto.TaskNoteOptions;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.propertyWorkLog.entity.vo.LogFieldType;
import com.backend.lab.domain.property.taskNote.entity.TaskNote;
import com.backend.lab.domain.property.taskNote.entity.dto.req.SearchTaskNoteOptions;
import com.backend.lab.domain.property.taskNote.entity.dto.req.TaskNoteCreateReq;
import com.backend.lab.domain.property.taskNote.entity.dto.req.TaskNoteReq;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteResp;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteSearchResp;
import com.backend.lab.domain.property.taskNote.entity.vo.TaskType;
import com.backend.lab.domain.property.taskNote.repository.TaskNoteRepository;
import com.backend.lab.domain.uploadFile.service.UploadFileService;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskNoteService {

  private final TaskNoteRepository taskNoteRepository;
  private final UploadFileService uploadFileService;

  //매물 생성시 업무일지 생성
  @Transactional
  public void createByProperty(TaskNoteReq taskNoteReq,
      Property property,
      Admin admin) {
    // TaskNote 객체 리스트 생성
    List<TaskNote> taskNotes = taskNoteReq.getTaskNoteReqs()
        .stream()
        .map(r -> TaskNote.builder()
            .property(property)
            .createdBy(admin)
            .content(r.getContent())
            .image(
                r.getImageId() != null
                    ? uploadFileService.getById(r.getImageId())
                    : null)
            .type(r.getTaskType())
            .build())
        .toList();

    // 일괄 저장
    taskNoteRepository.saveAll(taskNotes);
  }

  @Transactional
  public void updateByProperty(TaskNoteReq taskNoteReq, Property property, Admin admin) {
    List<TaskNote> existingTaskNotes = getByProperty(property.getId());

    // 요청에 포함된 id 수집
    Set<Long> requestIds = taskNoteReq.getTaskNoteReqs().stream()
        .map(PropertyTaskNoteUpdateReq::getId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    // 요청에 없는 기존 TaskNote들은 삭제
    List<TaskNote> toDelete = existingTaskNotes.stream()
        .filter(taskNote -> !requestIds.contains(taskNote.getId()))
        .collect(Collectors.toList());
    taskNoteRepository.deleteAll(toDelete);

    for (PropertyTaskNoteUpdateReq req : taskNoteReq.getTaskNoteReqs()) {
      Long id = req.getId();
      TaskNote taskNote;
      if (id == null) {
        taskNote = TaskNote.builder()
            .property(property)
            .createdBy(admin)
            .content(req.getContent())
            .image(
                req.getImageId() != null
                    ? uploadFileService.getById(req.getImageId())
                    : null
            )
            .type(req.getTaskType())
            .build();
      } else {
        taskNote = this.get(id);
        taskNote.setProperty(property);
        taskNote.setContent(req.getContent());
        taskNote.setType(req.getTaskType());
        if (req.getImageId() != null) {
          taskNote.setImage(uploadFileService.getById(req.getImageId()));
        } else {
          req.setImage(null);
        }
      }
      taskNoteRepository.save(taskNote);
    }
  }

  //기존 매물에 업무일지 생성
  @Transactional
  public void updateCreate(TaskNoteCreateReq req, Admin admin, Property property) {
    taskNoteRepository.save(
        TaskNote.builder()
            .property(property)
            .createdBy(admin)
            .content(req.getContent())
            .image(req.getImage())
            .type(req.getTaskType())
            .build());
  }

  @Transactional
  public void save(String before, String after, Property property, Admin admin,
      LogFieldType logFieldType) {
    taskNoteRepository.save(TaskNote.builder()
        .createdBy(admin)
        .type(TaskType.AUTO_UPDATE)
        .image(null)
        .property(property)
        .beforeValue(before)
        .afterValue(after)
        .logFieldType(logFieldType)
        .build());
  }

  public TaskNote get(Long taskNoteId) {
    return taskNoteRepository.findById(taskNoteId).orElse(null);
  }


  //매물 조회시 업무일지
  public List<TaskNote> getByProperty(Long propertyId) {
    return taskNoteRepository.findAllByPropertyId(propertyId);
  }

  public Page<TaskNote> getByProperty(TaskNoteOptions options) {
    return taskNoteRepository.findAllByPropertyId(options);
  }

  public Page<TaskNoteSearchResp> search(SearchTaskNoteOptions options) {
    return taskNoteRepository.search(options);
  }
  public List<TaskNote> getRecent() {
    return taskNoteRepository.findByOrderByCreatedAtDesc(20);
  }

  //매물 삭제시
  @Transactional
  public void deleteByProperty(Long propertyId) {
    List<TaskNote> taskNotes = this.getByProperty(propertyId);
    taskNoteRepository.deleteAll(taskNotes);
  }

  public TaskNoteResp taskNoteResp(TaskNote taskNote) {

    String createdBy = taskNote.getCreatedBy().getName() + (taskNote.getCreatedBy().getJobGrade() != null ? " " + taskNote.getCreatedBy().getJobGrade().getName() : "");
    boolean deletedAt = taskNote.getProperty().getDeletedAt() != null;

    return TaskNoteResp.builder()
        .id(taskNote.getId())
        .createdAt(taskNote.getCreatedAt())

        .propertyDeleted(deletedAt)
        .propertyId(taskNote.getProperty().getId())
        .createdId(taskNote.getCreatedBy().getId())
        .taskType(taskNote.getType())
        .content(taskNote.getContent())
        .createdBy(createdBy)
        .after(taskNote.getAfterValue())
        .before(taskNote.getBeforeValue())
        .image(uploadFileService.uploadFileResp(taskNote.getImage()))
        .build();

  }
  //개별 삭제시
  @Transactional
  public void delete(Long taskNoteId) {
    TaskNote taskNote = get(taskNoteId);
    taskNoteRepository.delete(taskNote);
  }
}
