package com.group1.app.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class MetadataKeyConfig {
    @Value("${metadata.key.pattern:^[A-Z_]+$}")
    private String keyPattern;
}
