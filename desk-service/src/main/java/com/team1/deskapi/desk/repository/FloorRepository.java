package com.team1.deskapi.desk.repository;

import com.team1.deskapi.desk.domain.Floor;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FloorRepository extends JpaRepository<Floor, UUID> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Floor> findForShareById(UUID id);

    boolean existsByNumber(String number);

    boolean existsByNumberAndIdNot(String number, UUID id);
}
