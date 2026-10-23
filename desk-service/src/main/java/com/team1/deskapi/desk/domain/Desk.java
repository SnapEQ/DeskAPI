package com.team1.deskapi.desk.domain;

import com.team1.deskapi.desk.domain.enums.DeskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Desk extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    private DeskStatus status;

    @Version
    @Column(nullable = false)
    private Long version;

    public boolean isActive() {
        return status == DeskStatus.ACTIVE;
    }

    public void activate() {
        this.status = DeskStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = DeskStatus.INACTIVE;
    }
}
