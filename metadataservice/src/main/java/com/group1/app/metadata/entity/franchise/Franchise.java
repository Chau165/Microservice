package com.group1.app.metadata.entity.franchise;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.group1.app.metadata.infrastructure.persistence.base.BaseEntity;
import com.group1.app.metadata.entity.franchisestaff.FranchiseStaff;
import com.group1.app.metadata.entity.brand.Brand;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "franchises", uniqueConstraints = @UniqueConstraint(columnNames = "franchiseCode"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Franchise extends BaseEntity {

    @Column(nullable = false)
    private String franchiseName;

    @Column(nullable = false, unique = true)
    private String franchiseCode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FranchiseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnboardingStatus onboardingStatus;

    @Column(nullable = false)
    private boolean featureFlags;

    // Stored as CHAR(36) in DB, so map UUID through CHAR to avoid SQL Server GUID extraction issues.
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID ownerId;

    private String contactInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "franchise", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<FranchiseStaff> franchiseStaffs;

}

