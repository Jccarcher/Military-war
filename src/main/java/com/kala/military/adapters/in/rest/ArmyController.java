package com.kala.military.adapters.in.rest;

import com.kala.military.application.dto.ArmyResponse;
import com.kala.military.application.dto.BattleRequest;
import com.kala.military.application.dto.BattleResultResponse;
import com.kala.military.application.dto.CreateArmyRequest;
import com.kala.military.application.dto.TrainUnitRequest;
import com.kala.military.application.dto.TransformUnitRequest;
import com.kala.military.application.ports.in.ArmyUseCasePort;
import com.kala.military.application.ports.in.BattleUseCasePort;
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

    private final ArmyUseCasePort armyApplicationService;
    private final BattleUseCasePort battleApplicationService;

    public ArmyController(ArmyUseCasePort armyApplicationService, BattleUseCasePort battleApplicationService) {
        this.armyApplicationService = armyApplicationService;
        this.battleApplicationService = battleApplicationService;
    }

    @PostMapping("/armies")
    public ResponseEntity<ArmyResponse> createArmy(@RequestBody CreateArmyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(armyApplicationService.createArmy(request));
    }

    @GetMapping("/armies/{id}")
    public ResponseEntity<ArmyResponse> getArmy(@PathVariable("id") String id) {
        return ResponseEntity.ok(armyApplicationService.getArmy(id));
    }

    @PostMapping("/armies/{id}/train")
    public ResponseEntity<ArmyResponse> trainUnit(@PathVariable("id") String id, @RequestBody TrainUnitRequest request) {
        TrainUnitRequest mergedRequest = new TrainUnitRequest(id, request.unitType());
        return ResponseEntity.ok(armyApplicationService.trainUnit(mergedRequest));
    }

    @PostMapping("/armies/{id}/transform")
    public ResponseEntity<ArmyResponse> transformUnit(@PathVariable("id") String id, @RequestBody TransformUnitRequest request) {
        TransformUnitRequest mergedRequest = new TransformUnitRequest(id, request.sourceType(), request.targetType());
        return ResponseEntity.ok(armyApplicationService.transformUnit(mergedRequest));
    }

    @PostMapping("/battle")
    public ResponseEntity<BattleResultResponse> simulateBattle(@RequestBody BattleRequest request) {
        return ResponseEntity.ok(battleApplicationService.simulateBattle(request));
    }
}
