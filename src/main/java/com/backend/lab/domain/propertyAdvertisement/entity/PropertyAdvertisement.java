package com.backend.lab.domain.propertyAdvertisement.entity;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.member.core.entity.Member;
import com.backend.lab.domain.property.core.entity.Property;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "property_advertisement")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLRestriction("deleted_at IS NULL AND agent_id IN(SELECT m.id FROM member m where m.deleted_at is NULL)")
@SQLDelete(sql = "UPDATE property_advertisement SET deleted_at = NOW() WHERE id = ?")
public class PropertyAdvertisement extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "property_id")
  private Property property;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "agent_id")
  private Member agent;

  private LocalDateTime startDate; // 광고 시작일
  private LocalDateTime endDate;   // 광고 종료일

}
