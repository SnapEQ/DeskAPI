package com.team1.deskapi.desk.mappers;

import com.team1.deskapi.desk.domain.Desk;
import com.team1.deskapi.desk.domain.Floor;
import com.team1.deskapi.desk.dtos.desk.CreateDeskRequest;
import com.team1.deskapi.desk.dtos.desk.DeskResponse;
import com.team1.deskapi.desk.dtos.desk.FullUpdateDeskRequest;
import com.team1.deskapi.desk.dtos.desk.PartialUpdateDeskRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DeskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "floor", source = "floor")
    @Mapping(target = "number", source = "request.number")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "version", ignore = true)
    Desk toDesk(CreateDeskRequest request, Floor floor);

    @Mapping(target = "floorId", source = "floor.id")
    DeskResponse toDeskResponse(Desk desk);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "floor", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateDesk(
            FullUpdateDeskRequest request,
            @MappingTarget Desk desk
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "floor", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateDesk(
            PartialUpdateDeskRequest request,
            @MappingTarget Desk desk
    );
}
