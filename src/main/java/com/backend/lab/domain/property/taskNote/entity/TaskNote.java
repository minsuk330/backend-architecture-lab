package com.backend.lab.domain.property.taskNote.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.property.core.entity.Property;
import com.backend.lab.domain.property.propertyWorkLog.entity.vo.LogFieldType;
import com.backend.lab.domain.property.taskNote.entity.vo.TaskType;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_note")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TaskNote extends BaseEntity {

  @JoinColumn(name = "create_by_admin_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Admin createdBy;

  @ManyToOne
  @JoinColumn(name = "property_id")
  private Property property;

  @Column(length = 2000)
  private String content;

  @JoinColumn(name = "image_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UploadFile image;

  //자동 생성용 필드들
  private String beforeValue;
  private String afterValue;
  private LogFieldType logFieldType;

  //업무일지용도
  @Enumerated(EnumType.STRING)
  private TaskType type;


}
