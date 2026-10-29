package com.team1.deskapi.desk.service.impl;

import com.team1.deskapi.desk.domain.Desk;
import com.team1.deskapi.desk.domain.Floor;
import com.team1.deskapi.desk.domain.Reservation;
import com.team1.deskapi.desk.domain.enums.ReservationStatus;
import com.team1.deskapi.desk.dtos.common.PageResult;
import com.team1.deskapi.desk.dtos.desk.CreateDeskRequest;
import com.team1.deskapi.desk.dtos.desk.DeskResponse;
import com.team1.deskapi.desk.dtos.desk.FullUpdateDeskRequest;
import com.team1.deskapi.desk.dtos.desk.PartialUpdateDeskRequest;
import com.team1.deskapi.desk.exceptions.ConflictException;
import com.team1.deskapi.desk.exceptions.DuplicateResourceException;
import com.team1.deskapi.desk.exceptions.InvalidRequestException;
import com.team1.deskapi.desk.exceptions.ResourceNotFoundException;
import com.team1.deskapi.desk.mappers.DeskMapper;
import com.team1.deskapi.desk.repository.DeskRepository;
import com.team1.deskapi.desk.repository.FloorRepository;
import com.team1.deskapi.desk.repository.ReservationRepository;
import com.team1.deskapi.desk.service.DeskService;
import lombok.RequiredArgsConstructor;
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
public class DeskServiceImpl implements DeskService {

    private final DeskRepository deskRepository;
    private final DeskMapper deskMapper;
    private final FloorRepository floorRepository;
    private final ReservationRepository reservationRepository;
    private final Clock clock;

    @Override
    @Transactional
    public DeskResponse createDesk(CreateDeskRequest request) {
        Floor floor = ensureFloorExistsAndIsActive(request.floorId());
        ensureUniqueNumber(request.number(), request.floorId());

        Desk desk = deskMapper.toDesk(request, floor);

        Desk savedDesk = deskRepository.save(desk);

        return deskMapper.toDeskResponse(savedDesk);
    }

    @Override
    @Transactional(readOnly = true)
    public DeskResponse getDeskById(UUID deskId) {
        Desk desk = ensureDeskExists(deskId);
        return deskMapper.toDeskResponse(desk);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<DeskResponse> getAllDesks(Pageable pageable) {
        return PageResult.from(deskRepository.findAll(pageable).map(deskMapper::toDeskResponse));
    }

    @Override
    @Transactional
    public DeskResponse updateDesk(UUID deskId, FullUpdateDeskRequest request) {
        Desk desk = ensureDeskExists(deskId);
        Floor floor = resolveTargetFloor(desk, request.floorId());
        ensureUniqueNumber(request.number(), floor.getId(), deskId);

        deskMapper.updateDesk(request, desk);

        desk.setFloor(floor);

        deskRepository.flush();

        return deskMapper.toDeskResponse(desk);
    }

    @Override
    @Transactional
    public DeskResponse updateDesk(UUID deskId, PartialUpdateDeskRequest request) {
        ensurePatchHasChanges(request);
        Desk desk = ensureDeskExists(deskId);

        Floor floor = resolveTargetFloor(desk, request.floorId());
        String number = request.number() != null ? request.number() : desk.getNumber();
        ensureUniqueNumber(number, floor.getId(), deskId);

        deskMapper.updateDesk(request, desk);
        desk.setFloor(floor);

        deskRepository.flush();

        return deskMapper.toDeskResponse(desk);
    }

    @Override
    @Transactional
    public DeskResponse activateDesk(UUID deskId) {
        Desk desk = ensureDeskExists(deskId);
        ensureFloorExistsAndIsActive(desk.getFloor().getId());

        if (desk.isActive()) {
            throw new ConflictException("Desk is already active");
        }

        desk.activate();

        deskRepository.flush();

        return deskMapper.toDeskResponse(desk);
    }

    @Override
    @Transactional
    public DeskResponse deactivateDesk(UUID deskId) {
        Desk desk = ensureDeskExists(deskId);

        if (!desk.isActive()) {
            throw new ConflictException("Desk is already inactive");
        }

        desk.deactivate();

        deskRepository.flush();

        LocalDate today = LocalDate.now(clock);

        List<Reservation> reservations = reservationRepository.findAllByDeskIdAndStatusAndReservationDateGreaterThanEqual(
                desk.getId(),
                ReservationStatus.BOOKED,
                today
        );

        Instant now = Instant.now(clock);

        reservations.forEach(reservation -> reservation.cancel(now));

        reservationRepository.flush();

        return deskMapper.toDeskResponse(desk);
    }

    private void ensureUniqueNumber(String number, UUID floorId, UUID id) {
        if(deskRepository.existsByNumberAndFloorIdAndIdNot(number, floorId, id)) {
            throw new DuplicateResourceException("Desk with that number already exists on that floor");
        }
    }

    private void ensureUniqueNumber(String number, UUID floorId) {
        if(deskRepository.existsByNumberAndFloorId(number, floorId)) {
            throw new DuplicateResourceException("Desk with that number already exists on that floor");
        }
    }

    private Floor ensureFloorExistsAndIsActive(UUID floorId) {
        Floor floor = floorRepository.findForShareById(floorId)
                .orElseThrow(() -> new ResourceNotFoundException("Floor with that id does not exist"));
        if (!floor.isActive()) {
            throw new ConflictException("Floor is inactive");
        }
        return floor;
    }

    private Desk ensureDeskExists(UUID id) {
        return deskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Desk with that id does not exist"));
    }

    private void ensurePatchHasChanges(PartialUpdateDeskRequest request) {
        if (request.number() == null && request.description() == null && request.floorId() == null) {
            throw new InvalidRequestException("At least one desk field must be provided");
        }
    }

    private Floor resolveTargetFloor(Desk desk, UUID floorId) {
        if (floorId == null || floorId.equals(desk.getFloor().getId())) {
            return desk.getFloor();
        }

        return ensureFloorExistsAndIsActive(floorId);
    }
}
