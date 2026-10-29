package com.team1.deskapi.desk.service;

import com.team1.deskapi.desk.dtos.common.PageResult;
import com.team1.deskapi.desk.dtos.desk.CreateDeskRequest;
import com.team1.deskapi.desk.dtos.desk.DeskResponse;
import com.team1.deskapi.desk.dtos.desk.FullUpdateDeskRequest;
import com.team1.deskapi.desk.dtos.desk.PartialUpdateDeskRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DeskService {

    DeskResponse createDesk(CreateDeskRequest request);

    DeskResponse getDeskById(UUID deskId);

    PageResult<DeskResponse> getAllDesks(Pageable pageable);

    DeskResponse updateDesk(UUID deskId, FullUpdateDeskRequest request);

    DeskResponse updateDesk(UUID deskId, PartialUpdateDeskRequest request);

    DeskResponse activateDesk(UUID deskId);

    DeskResponse deactivateDesk(UUID deskId);
}
