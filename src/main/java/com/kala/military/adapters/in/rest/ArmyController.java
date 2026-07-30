package com.kala.military.adapters.in.rest;

import com.kala.military.application.dto.ArmyResponse;
import com.kala.military.application.dto.BattleRequest;
import com.kala.military.application.dto.BattleResultResponse;
import com.kala.military.application.dto.CreateArmyRequest;
import com.kala.military.application.dto.TrainUnitRequest;
import com.kala.military.application.dto.TransformUnitRequest;
import com.kala.military.application.ports.in.ArmyUseCasePort;
import com.kala.military.application.ports.in.BattleUseCasePort;

import jakarta.validation.Valid;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Inbound REST adapter. Validates the payload format and delegates to the inbound ports. */
@RestController
@RequestMapping("/api/v1")
public final class ArmyController {

    private static final Logger logger = LoggerFactory.getLogger(ArmyController.class);

    @NonNull
    private final ArmyUseCasePort armyApplicationService;

    @NonNull
    private final BattleUseCasePort battleApplicationService;

    public ArmyController(@NonNull ArmyUseCasePort armyApplicationService,
                          @NonNull BattleUseCasePort battleApplicationService) {
        this.armyApplicationService = armyApplicationService;
        this.battleApplicationService = battleApplicationService;
    }

    @PostMapping("/armies")
    @NonNull
    public ResponseEntity<ArmyResponse> createArmy(@Valid @RequestBody @NonNull CreateArmyRequest request) {
        logger.info("Creating army for civilization: {}", request.civilization());
        var response = armyApplicationService.createArmy(request);
        logger.info("Army created with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/armies/{id}")
    @NonNull
    public ResponseEntity<ArmyResponse> getArmy(@PathVariable("id") @NonNull String id) {
        logger.info("Retrieving army with id: {}", id);
        var response = armyApplicationService.getArmy(id);
        logger.info("Army retrieved with id: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/armies/{id}/train")
    @NonNull
    public ResponseEntity<ArmyResponse> trainUnit(@PathVariable("id") @NonNull String id,
                                                  @Valid @RequestBody @NonNull TrainUnitRequest request) {
        logger.info("Training unit {} for army {}", request.unitType(), id);
        var mergedRequest = new TrainUnitRequest(id, request.unitType());
        var response = armyApplicationService.trainUnit(mergedRequest);
        logger.info("Training completed for army {}", response.id());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/armies/{id}/transform")
    @NonNull
    public ResponseEntity<ArmyResponse> transformUnit(@PathVariable("id") @NonNull String id,
                                                      @Valid @RequestBody @NonNull TransformUnitRequest request) {
        logger.info("Transforming unit from {} to {} for army {}", request.sourceType(), request.targetType(), id);
        var mergedRequest = new TransformUnitRequest(id, request.sourceType(), request.targetType());
        var response = armyApplicationService.transformUnit(mergedRequest);
        logger.info("Transformation completed for army {}", response.id());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/battle")
    @NonNull
    public ResponseEntity<BattleResultResponse> simulateBattle(@Valid @RequestBody @NonNull BattleRequest request) {
        logger.info("Simulating battle between armies {} and {}", request.firstArmyId(), request.secondArmyId());
        var response = battleApplicationService.simulateBattle(request);
        logger.info("Battle simulation completed with result: {}", response.result());
        return ResponseEntity.ok(response);
    }
}
