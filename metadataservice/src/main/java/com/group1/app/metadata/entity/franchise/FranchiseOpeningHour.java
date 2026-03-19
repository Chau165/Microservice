package com.group1.app.metadata.entity.franchise;

import com.group1.app.metadata.enums.DayOfWeekValue;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "franchise_opening_hours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FranchiseOpeningHour {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private DayOfWeekValue dayOfWeek;

    @Column(name = "open_time", columnDefinition = "time")
    private LocalTime openTime;

    @Column(name = "close_time", columnDefinition = "time")
    private LocalTime closeTime;


    @Column(name = "is_closed")
    private Boolean isClosed;

}
