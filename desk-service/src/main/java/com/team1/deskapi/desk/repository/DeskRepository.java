package com.team1.deskapi.desk.repository;

import com.team1.deskapi.desk.domain.Desk;
import com.team1.deskapi.desk.domain.enums.DeskStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeskRepository extends JpaRepository<Desk, UUID> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Desk> findForShareById(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Desk> findAllByFloorIdAndStatus(UUID floorId, DeskStatus status);

    boolean existsByNumberAndFloorId(String number, UUID floorId);

    boolean existsByNumberAndFloorIdAndIdNot(String number, UUID floorId, UUID id);

}
