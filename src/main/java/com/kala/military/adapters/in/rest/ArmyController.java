package com.kala.military.adapters.in.rest;

import com.kala.military.application.dto.ArmyResponse;
import com.kala.military.application.dto.BattleRequest;
import com.kala.military.application.dto.BattleResultResponse;
import com.kala.military.application.dto.CreateArmyRequest;
import com.kala.military.application.dto.TrainUnitRequest;
import com.kala.military.application.dto.TransformUnitRequest;
import com.kala.military.application.ports.in.ArmyUseCasePort;
import com.kala.military.application.ports.in.BattleUseCasePort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ArmyController {

    private static final Logger LOGGER = LogManager.getLogger(ArmyController.class);

    private final ArmyUseCasePort armyApplicationService;
    private final BattleUseCasePort battleApplicationService;

    public ArmyController(ArmyUseCasePort armyApplicationService, BattleUseCasePort battleApplicationService) {
        this.armyApplicationService = armyApplicationService;
        this.battleApplicationService = battleApplicationService;
    }

    @PostMapping("/armies")
    public ResponseEntity<ArmyResponse> createArmy(@RequestBody CreateArmyRequest request) {
        LOGGER.info("Creating army for civilization: {}", request.civilization());
        ArmyResponse response = armyApplicationService.createArmy(request);
        LOGGER.info("Army created with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/armies/{id}")
    public ResponseEntity<ArmyResponse> getArmy(@PathVariable("id") String id) {
        LOGGER.info("Retrieving army with id: {}", id);
        ArmyResponse response = armyApplicationService.getArmy(id);
        LOGGER.info("Army retrieved with id: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/armies/{id}/train")
    public ResponseEntity<ArmyResponse> trainUnit(@PathVariable("id") String id, @RequestBody TrainUnitRequest request) {
        LOGGER.info("Training unit {} for army {}", request.unitType(), id);
        TrainUnitRequest mergedRequest = new TrainUnitRequest(id, request.unitType());
        ArmyResponse response = armyApplicationService.trainUnit(mergedRequest);
        LOGGER.info("Training completed for army {}", response.id());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/armies/{id}/transform")
    public ResponseEntity<ArmyResponse> transformUnit(@PathVariable("id") String id, @RequestBody TransformUnitRequest request) {
        LOGGER.info("Transforming unit from {} to {} for army {}", request.sourceType(), request.targetType(), id);
        TransformUnitRequest mergedRequest = new TransformUnitRequest(id, request.sourceType(), request.targetType());
        ArmyResponse response = armyApplicationService.transformUnit(mergedRequest);
        LOGGER.info("Transformation completed for army {}", response.id());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/battle")
    public ResponseEntity<BattleResultResponse> simulateBattle(@RequestBody BattleRequest request) {
        LOGGER.info("Simulating battle between armies {} and {}", request.firstArmyId(), request.secondArmyId());
        BattleResultResponse response = battleApplicationService.simulateBattle(request);
        LOGGER.info("Battle simulation completed with result: {}", response.result());
        return ResponseEntity.ok(response);
    }
}
