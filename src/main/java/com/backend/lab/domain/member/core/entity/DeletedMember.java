package com.backend.lab.domain.member.core.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.entity.embedded.BuyerProperties;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.embedded.SellerProperties;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLRestriction("deleted_at IS NOT NULL")
@FilterDef(name = "deletedPersistentMemberFilter", defaultCondition = "delete_persistent_at IS NULL", autoEnabled = true)
@Filter(name = "deletedPersistentMemberFilter")
@SQLDelete(sql = "UPDATE member SET delete_persistent_at = NOW() WHERE id = ?")
public class DeletedMember extends BaseEntity {

  @Column(name = "delete_persistent_at", nullable = true)
  private LocalDateTime delete_persistent_at;

  private String email;
  private String password;

  @JoinColumn(name = "profile_image_file_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private UploadFile profileImage;

  private String di;

  @Enumerated(EnumType.STRING)
  private ProviderType provider;
  private String providerId;

  private boolean isFirstLogin;
  private boolean isBlocked;

  @Enumerated(EnumType.STRING)
  private MemberType type;

  @Embedded
  private CustomerProperties customerProperties;

  @Embedded
  private AgentProperties agentProperties;

  @Embedded
  private BuyerProperties buyerProperties;

  @Embedded
  private SellerProperties sellerProperties;
}
