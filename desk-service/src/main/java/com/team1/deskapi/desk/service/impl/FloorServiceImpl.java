package com.team1.deskapi.desk.service.impl;

import com.team1.deskapi.desk.domain.Desk;
import com.team1.deskapi.desk.domain.Floor;
import com.team1.deskapi.desk.domain.Reservation;
import com.team1.deskapi.desk.domain.enums.DeskStatus;
import com.team1.deskapi.desk.domain.enums.ReservationStatus;
import com.team1.deskapi.desk.dtos.common.PageResult;
import com.team1.deskapi.desk.dtos.floor.CreateFloorRequest;
import com.team1.deskapi.desk.dtos.floor.FloorResponse;
import com.team1.deskapi.desk.dtos.floor.UpdateFloorRequest;
import com.team1.deskapi.desk.exceptions.ConflictException;
import com.team1.deskapi.desk.exceptions.DuplicateResourceException;
import com.team1.deskapi.desk.exceptions.ResourceNotFoundException;
import com.team1.deskapi.desk.mappers.FloorMapper;
import com.team1.deskapi.desk.repository.DeskRepository;
import com.team1.deskapi.desk.repository.FloorRepository;
import com.team1.deskapi.desk.repository.ReservationRepository;
import com.team1.deskapi.desk.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FloorServiceImpl implements FloorService {

    private final Clock clock;
    private final FloorRepository floorRepository;
    private final DeskRepository deskRepository;
    private final FloorMapper floorMapper;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public FloorResponse createFloor(CreateFloorRequest request) {
        ensureUniqueNumber(request.number());

        Floor floor = floorMapper.toFloor(request);

        Floor savedFloor = floorRepository.save(floor);

        return floorMapper.toFloorResponse(savedFloor);
    }

    @Override
    @Transactional(readOnly = true)
    public FloorResponse getFloorById(UUID floorId) {
        Floor floor = ensureFloorExists(floorId);

        return floorMapper.toFloorResponse(floor);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<FloorResponse> getAllFloors(Pageable pageable) {
        Page<Floor> floors = floorRepository.findAll(pageable);

        return PageResult.from(floors.map(floorMapper::toFloorResponse));
    }

    @Override
    @Transactional
    public FloorResponse updateFloor(UUID floorId, UpdateFloorRequest request) {
        Floor floor = ensureFloorExists(floorId);

        ensureUniqueNumber(request.number(), floorId);

        floorMapper.updateFloor(request, floor);

        floorRepository.flush();

        return floorMapper.toFloorResponse(floor);
    }

    @Override
    @Transactional
    public FloorResponse deactivateFloor(UUID floorId) {
        Floor floor = ensureFloorExists(floorId);

        if (!floor.isActive()) {
            throw new ConflictException("Floor is already inactive");
        }

        floor.deactivate();
        floorRepository.saveAndFlush(floor);

        List<Desk> desks = deskRepository.findAllByFloorIdAndStatus(floorId, DeskStatus.ACTIVE);
        desks.forEach(Desk::deactivate);
        deskRepository.saveAllAndFlush(desks);

        LocalDate today = LocalDate.now(clock);
        Instant now = Instant.now(clock);

        List<Reservation> reservations = reservationRepository
                .findAllByDeskFloorIdAndStatusAndReservationDateGreaterThanEqual(
                        floorId,
                        ReservationStatus.BOOKED,
                        today
                );
        reservations.forEach(reservation -> reservation.cancel(now));

        reservationRepository.flush();

        return floorMapper.toFloorResponse(floor);
    }

    @Override
    @Transactional
    public FloorResponse activateFloor(UUID floorId) {
        Floor floor = ensureFloorExists(floorId);
        if (floor.isActive()) {
            throw new ConflictException("Floor is already active");
        }
        floor.activate();
        reservationRepository.flush();
        return floorMapper.toFloorResponse(floor);
    }

    private void ensureUniqueNumber(String number) {
        if (floorRepository.existsByNumber(number)) {
            throw new DuplicateResourceException("Floor number must be unique");
        }
    }

    private void ensureUniqueNumber(String number, UUID id) {
        if (floorRepository.existsByNumberAndIdNot(number, id)) {
            throw new DuplicateResourceException("Floor number must be unique");
        }
    }

    private Floor ensureFloorExists(UUID floorId) {
        return floorRepository.findById(floorId)
                .orElseThrow(() -> new ResourceNotFoundException("Floor not found"));
    }
}
