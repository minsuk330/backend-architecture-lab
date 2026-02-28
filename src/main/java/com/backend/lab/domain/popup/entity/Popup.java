package com.backend.lab.domain.popup.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "popup")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Popup extends BaseEntity {

  @JoinColumn(name = "create_by_admin_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Admin createdBy;

  private Boolean canAnonymousView;
  private Boolean canAgentView;
  private Boolean canBuyerView;
  private Boolean canSellerView;

  @JoinColumn(name = "image_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UploadFile image;

  @Column(length = 2000)
  private String url;

  private LocalDate startAt;
  private LocalDate endAt;
}
