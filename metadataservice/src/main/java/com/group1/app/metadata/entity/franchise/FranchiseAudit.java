package com.group1.app.metadata.entity.franchise;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "franchise_audits")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID franchiseId;

    private String fieldChanged;

    private String oldValue;

    private String newValue;

    private String changedBy;

    private LocalDateTime timestamp;
}

