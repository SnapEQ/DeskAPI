package com.team1.deskapi.desk.service.impl;

import com.team1.deskapi.desk.domain.Desk;
import com.team1.deskapi.desk.domain.Reservation;
import com.team1.deskapi.desk.domain.enums.ReservationStatus;
import com.team1.deskapi.desk.dtos.reservation.CreateReservationRequest;
import com.team1.deskapi.desk.dtos.reservation.FullUpdateReservationRequest;
import com.team1.deskapi.desk.dtos.reservation.PartialUpdateReservationRequest;
import com.team1.deskapi.desk.dtos.reservation.ReservationResponse;
import com.team1.deskapi.desk.exceptions.ConflictException;
import com.team1.deskapi.desk.exceptions.DuplicateResourceException;
import com.team1.deskapi.desk.exceptions.InvalidRequestException;
import com.team1.deskapi.desk.exceptions.ResourceNotFoundException;
import com.team1.deskapi.desk.mappers.ReservationMapper;
import com.team1.deskapi.desk.repository.DeskRepository;
import com.team1.deskapi.desk.repository.ReservationRepository;
import com.team1.deskapi.desk.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final DeskRepository deskRepository;
    private final ReservationMapper reservationMapper;
    private final Clock clock;

    @Override
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request, UUID userId) {

        Desk desk = ensureDeskExists(request.deskId());
        ensureDeskIsActive(desk);
        ensureNoConflictingReservation(
                desk.getId(),
                request.reservationDate(),
                userId
        );

        Reservation reservation = reservationMapper.toReservation(request, desk, userId);
        reservationRepository.save(reservation);

        return reservationMapper.toReservationResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(UUID reservationId, FullUpdateReservationRequest request) {

        Reservation reservation = ensureReservationExistsAndIsBooked(reservationId);
        LocalDate today = LocalDate.now(clock);
        ensureValidDateForUpdate(reservation.getReservationDate(), today);

        Desk desk = ensureDeskExists(request.deskId());
        ensureDeskIsActive(desk);

        ensureNoConflictingReservation(
                desk.getId(),
                request.reservationDate(),
                reservation.getUserId(),
                reservationId
        );

        reservationMapper.update(request, reservation);

        reservation.setDesk(desk);

        reservationRepository.flush();

        return reservationMapper.toReservationResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(UUID reservationId, PartialUpdateReservationRequest request) {

        ensurePatchHasChanges(request);

        Reservation reservation = ensureReservationExistsAndIsBooked(reservationId);
        LocalDate today = LocalDate.now(clock);

        LocalDate date = request.reservationDate() == null ? reservation.getReservationDate() : request.reservationDate();

        ensureValidDateForUpdate(reservation.getReservationDate(), today);

        Desk desk = request.deskId() == null ? reservation.getDesk() : ensureDeskExists(request.deskId());
        ensureDeskIsActive(desk);

        ensureNoConflictingReservation(
                desk.getId(),
                date,
                reservation.getUserId(),
                reservationId
        );

        reservationMapper.update(request, reservation);

        reservation.setDesk(desk);

        reservationRepository.flush();

        return reservationMapper.toReservationResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse cancelReservation(UUID reservationId) {
        Reservation reservation = ensureReservationExistsAndIsBooked(reservationId);
        Instant now = Instant.now(clock);
        ensureValidDateForCancel(reservation.getReservationDate(), LocalDate.now(clock));
        reservation.cancel(now);
        reservationRepository.flush();
        return reservationMapper.toReservationResponse(reservation);
    }

    private void ensureNoConflictingReservation(
            UUID deskId,
            LocalDate reservationDate,
            UUID userId
    ) {
        if (reservationRepository.existsByDeskIdAndReservationDateAndStatus(
                deskId,
                reservationDate,
                ReservationStatus.BOOKED
        )) {
            throw new DuplicateResourceException("Reservation for that desk already exists");
        }

        if (reservationRepository.existsByUserIdAndReservationDateAndStatus(
                userId,
                reservationDate,
                ReservationStatus.BOOKED
        )) {
            throw new DuplicateResourceException("Reservation for that date already exists");
        }
    }

    private void ensureNoConflictingReservation(
            UUID deskId,
            LocalDate reservationDate,
            UUID userId,
            UUID reservationId
    ) {
        if (reservationRepository.existsByDeskIdAndReservationDateAndStatusAndIdNot(
                deskId,
                reservationDate,
                ReservationStatus.BOOKED,
                reservationId
        )) {
            throw new DuplicateResourceException("Reservation for that desk already exists");
        }

        if (reservationRepository.existsByUserIdAndReservationDateAndStatusAndIdNot(
                userId,
                reservationDate,
                ReservationStatus.BOOKED,
                reservationId
        )) {
            throw new DuplicateResourceException("Reservation for that date already exists");
        }
    }

    private Desk ensureDeskExists(UUID deskId) {
        return deskRepository.findForShareById(deskId)
                .orElseThrow(() -> new ResourceNotFoundException("Desk not found"));
    }

    private void ensureDeskIsActive(Desk desk) {
        if (!desk.isActive()) {
            throw new ConflictException("Desk is inactive");
        }
    }

    private Reservation ensureReservationExistsAndIsBooked(UUID reservationId) {

        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (!res.isBooked()) {
            throw new ConflictException("Reservation is cancelled");
        }

        return res;
    }

    private void ensureValidDateForUpdate(LocalDate reservationDate, LocalDate today) {
        if (!reservationDate.isAfter(today)) {
            throw new ConflictException("Only future reservations can be updated");
        }
    }

    private void ensureValidDateForCancel(LocalDate reservationDate, LocalDate today) {
        if (reservationDate.isBefore(today)) {
            throw new ConflictException("Only future reservations can be cancelled");
        }
    }

    private void ensurePatchHasChanges(PartialUpdateReservationRequest request) {
        if (request.deskId() == null && request.reservationDate() == null) {
            throw new InvalidRequestException("At least one reservation field must be provided");
        }
    }
}
