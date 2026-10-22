package com.team1.deskapi.desk.repository;

import com.team1.deskapi.desk.domain.Desk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeskRepository extends JpaRepository<Desk, UUID> {
}
