package com.backend.lab.domain.property.taskNote.repository;


import com.backend.lab.api.admin.tasknote.dto.TaskNoteOptions;
import com.backend.lab.domain.property.taskNote.entity.TaskNote;
import com.backend.lab.domain.property.taskNote.entity.dto.req.SearchTaskNoteOptions;
import com.backend.lab.domain.property.taskNote.entity.dto.resp.TaskNoteSearchResp;
import org.springframework.data.domain.Page;


public interface TaskNoteRepositoryCustom {
  Page<TaskNoteSearchResp> search(SearchTaskNoteOptions options);

  Page<TaskNote> findAllByPropertyId(TaskNoteOptions options);
}
