package com.airops.wms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "locations", schema = "wms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 10)
    private String zone;

    @Column(nullable = false, length = 10)
    private String aisle;

    @Column(nullable = false, length = 10)
    private String bay;

    @Column(nullable = false, length = 10)
    private String level;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer occupied;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public String getLocationCode() {
        return zone + "-" + aisle + "-" + bay + "-" + level;
    }

    public int getAvailableCapacity() {
        return capacity - occupied;
    }
}
