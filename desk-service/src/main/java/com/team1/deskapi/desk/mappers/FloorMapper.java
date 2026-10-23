package com.team1.deskapi.desk.mappers;

import com.team1.deskapi.desk.domain.Floor;
import com.team1.deskapi.desk.dtos.floor.CreateFloorRequest;
import com.team1.deskapi.desk.dtos.floor.FloorResponse;
import com.team1.deskapi.desk.dtos.floor.UpdateFloorRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FloorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    Floor toFloor(CreateFloorRequest request);

    FloorResponse toFloorResponse(Floor floor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateFloor(
            UpdateFloorRequest request,
            @MappingTarget Floor floor);
}
