package org.example.warehouseservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "request")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "status")
    private String status;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "handled_by")
    private Long handledBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "franchise_id")
    private Long franchiseId;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "item_name")
    private String itemName;
}