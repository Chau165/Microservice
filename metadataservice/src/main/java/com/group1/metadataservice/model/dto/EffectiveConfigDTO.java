package com.group1.metadataservice.model.dto;

import com.group1.metadataservice.common.validation.ValidMetadataKey;
import com.group1.metadataservice.model.entity.BaseConfig.ConfigType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EffectiveConfigDTO implements Serializable {

    @ValidMetadataKey
    private String configKey;
    private String configValue;
    private ConfigType configType;
    private String configGroup;
    private String description;
    private String regionCode;
    private Boolean enabled;
    private Boolean isOverridden;
}
