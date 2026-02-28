package com.backend.lab.domain.property.core.entity.information;

import com.backend.lab.common.entity.BaseEntity;
import com.backend.lab.domain.property.core.entity.embedded.RegisterProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "register_information")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RegisterInformation extends BaseEntity {

  private RegisterProperties properties;
}
