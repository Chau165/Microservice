package com.group1.app.metadata.entity.baseconfig;

import com.group1.app.common.validation.ValidMetadataKey;
import com.group1.app.metadata.infrastructure.persistence.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "base_config")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseConfig extends BaseEntity {

    @Column(nullable = false, unique = true)
    @ValidMetadataKey
    private String configKey;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String configValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfigType configType;

    private String configGroup;

    private String description;
}
