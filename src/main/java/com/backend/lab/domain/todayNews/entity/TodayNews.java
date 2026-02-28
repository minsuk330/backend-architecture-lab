package com.backend.lab.domain.todayNews.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.admin.core.entity.Admin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TodayNews extends BaseEntity {

  @JoinColumn(name = "create_by_admin_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Admin createdBy;

  private String title;
  private String company;
  @Column(length = 3000)
  private String url;
}
