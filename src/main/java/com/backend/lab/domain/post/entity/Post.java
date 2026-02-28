package com.backend.lab.domain.post.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.admin.core.entity.Admin;
import com.backend.lab.domain.post.entity.vo.PostType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

  @JoinColumn(name = "create_by_admin_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Admin createdBy;

  private Boolean canAgentView;
  private Boolean canBuyerView;
  private Boolean canSellerView;

  @Enumerated(EnumType.STRING)
  private PostType type;
  private String title;
  @Lob
  private String content;
}
