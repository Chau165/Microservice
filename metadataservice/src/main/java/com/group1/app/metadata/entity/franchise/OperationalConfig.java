package com.group1.app.metadata.entity.franchise;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "operational_configs")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OperationalConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID franchiseId;

    private boolean openingHoursConfigured;

    private boolean menuProfileAssigned;

    private boolean warehouseMappingConfigured;

    private boolean posEnabled;

    private boolean orderingEnabled;

    private boolean autoOrderEnabled;

    @Column(name = "menu_profile_id")
    private UUID menuProfileId;
}

