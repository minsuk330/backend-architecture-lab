package com.backend.lab.domain.member.core.entity;

import com.backend.lab.common.auth.AuthenticateUser;
import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.member.core.entity.embedded.AgentProperties;
import com.backend.lab.domain.member.core.entity.embedded.BuyerProperties;
import com.backend.lab.domain.member.core.entity.embedded.CustomerProperties;
import com.backend.lab.domain.member.core.entity.embedded.SellerProperties;
import com.backend.lab.domain.member.core.entity.vo.MemberType;
import com.backend.lab.domain.member.core.entity.vo.ProviderType;
import com.backend.lab.domain.memberNote.entity.MemberNote;
import com.backend.lab.domain.uploadFile.entity.UploadFile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@FilterDef(name = "deletedMemberFilter", defaultCondition = "deleted_at IS NULL", autoEnabled = true)
@Filter(name = "deletedMemberFilter")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
public class Member extends BaseEntity implements AuthenticateUser {

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

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
  @Builder.Default
  private Set<MemberNote> memberNotes = new HashSet<>();

  @Override
  public String[] getAuthorities() {
    if (type == null) {
      return new String[]{"ROLE_MEMBER"};
    }
    return type.getRoleNames();
  }
}
