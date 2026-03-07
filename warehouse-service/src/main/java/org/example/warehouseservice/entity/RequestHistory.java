    package org.example.warehouseservice.entity;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "request_history")
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class RequestHistory {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "history_id")
        private Long historyId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "request_id")
        private Request request;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "item_id")
        private Item item;


        @Column(name = "completed_date")
        private LocalDateTime completedDate;
    }