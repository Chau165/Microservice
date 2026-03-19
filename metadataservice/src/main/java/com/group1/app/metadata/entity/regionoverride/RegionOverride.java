package com.group1.app.metadata.entity.regionoverride;

import com.group1.app.metadata.infrastructure.persistence.base.BaseEntity;
import com.group1.app.metadata.entity.baseconfig.BaseConfig;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "region_override",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"base_config_id", "regionCode"})
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RegionOverride extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "base_config_id", nullable = false)
    private BaseConfig baseConfig;

    @Column(nullable = false)
    private String regionCode;

    @Column(columnDefinition = "TEXT")
    private String overrideValue;

    private Boolean enabled;
}
