package com.team1.deskapi.desk.repository;

import com.team1.deskapi.desk.domain.Reservation;
import com.team1.deskapi.desk.domain.enums.ReservationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Reservation> findAllByDeskFloorIdAndStatusAndReservationDateGreaterThanEqual(
            UUID floorId,
            ReservationStatus status,
            LocalDate reservationDate
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Reservation> findAllByDeskIdAndStatusAndReservationDateGreaterThanEqual(
            UUID deskId,
            ReservationStatus status,
            LocalDate reservationDate
    );

    boolean existsByDeskIdAndReservationDateAndStatus(
            UUID deskId,
            LocalDate reservationDate,
            ReservationStatus status
    );

    boolean existsByUserIdAndReservationDateAndStatus(
            UUID userId,
            LocalDate reservationDate,
            ReservationStatus status
    );

    boolean existsByDeskIdAndReservationDateAndStatusAndIdNot(
            UUID deskId,
            LocalDate reservationDate,
            ReservationStatus status,
            UUID id
    );

    boolean existsByUserIdAndReservationDateAndStatusAndIdNot(
            UUID userId,
            LocalDate reservationDate,
            ReservationStatus status,
            UUID id
    );


}
