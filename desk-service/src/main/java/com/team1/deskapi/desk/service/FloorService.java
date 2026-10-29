package com.team1.deskapi.desk.service;

import com.team1.deskapi.desk.dtos.common.PageResult;
import com.team1.deskapi.desk.dtos.floor.CreateFloorRequest;
import com.team1.deskapi.desk.dtos.floor.FloorResponse;
import com.team1.deskapi.desk.dtos.floor.UpdateFloorRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FloorService {

    FloorResponse createFloor(CreateFloorRequest request);

    FloorResponse getFloorById(UUID floorId);

    PageResult<FloorResponse> getAllFloors(Pageable pageable);

    FloorResponse updateFloor(UUID floorId, UpdateFloorRequest request);

    FloorResponse deactivateFloor(UUID floorId);

    FloorResponse activateFloor(UUID floorId);
}
