package com.team1.deskapi.desk.mappers;

import com.team1.deskapi.desk.domain.Desk;
import com.team1.deskapi.desk.domain.Reservation;
import com.team1.deskapi.desk.dtos.reservation.CreateReservationRequest;
import com.team1.deskapi.desk.dtos.reservation.FullUpdateReservationRequest;
import com.team1.deskapi.desk.dtos.reservation.PartialUpdateReservationRequest;
import com.team1.deskapi.desk.dtos.reservation.ReservationResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ReservationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "desk", source = "desk")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Reservation toReservation(CreateReservationRequest request, Desk desk, UUID userId);

    @Mapping(target = "deskId", source = "desk.id")
    ReservationResponse toReservationResponse(Reservation reservation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "desk", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    void update(FullUpdateReservationRequest request, @MappingTarget Reservation reservation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "desk", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    void update(PartialUpdateReservationRequest request, @MappingTarget Reservation reservation);
}
