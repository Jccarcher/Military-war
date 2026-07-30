package com.kala.military.application.services;

import com.kala.military.application.dto.ArmyResponse;
import com.kala.military.application.dto.CreateArmyRequest;
import com.kala.military.application.dto.TrainUnitRequest;
import com.kala.military.application.dto.TransformUnitRequest;
import com.kala.military.application.dto.UnitResponse;
import com.kala.military.application.ports.in.ArmyUseCasePort;
import com.kala.military.domain.Army;
import com.kala.military.domain.Unit;
import com.kala.military.ports.out.ArmyRepositoryPort;

import java.util.ArrayList;
import java.util.List;

public final class ArmyApplicationService implements ArmyUseCasePort {

    private final ArmyRepositoryPort armyRepositoryPort;

    public ArmyApplicationService(ArmyRepositoryPort armyRepositoryPort) {
        this.armyRepositoryPort = armyRepositoryPort;
    }

    public ArmyResponse createArmy(CreateArmyRequest request) {
        Army army = Army.create(request.civilization());
        armyRepositoryPort.save(army);
        return toResponse(army);
    }

    public ArmyResponse trainUnit(TrainUnitRequest request) {
        Army army = requireArmy(request.armyId());
        if (!army.hasUnitType(request.unitType())) {
            throw new IllegalArgumentException("Unidad no encontrada");
        }

        int trainingCost = army.getTrainingCost(request.unitType());
        if (army.getGold() < trainingCost) {
            throw new IllegalArgumentException("Oro insuficiente");
        }

        army.spendGold(trainingCost);
        army.trainUnit(request.unitType());
        armyRepositoryPort.save(army);
        return toResponse(army);
    }

    public ArmyResponse transformUnit(TransformUnitRequest request) {
        Army army = requireArmy(request.armyId());
        int transformationCost = army.getTransformationCost(request.sourceType(), request.targetType());
        if (army.getGold() < transformationCost) {
            throw new IllegalArgumentException("Oro insuficiente");
        }

        army.spendGold(transformationCost);
        army.transformUnit(request.sourceType(), request.targetType());
        armyRepositoryPort.save(army);
        return toResponse(army);
    }

    public ArmyResponse getArmy(String armyId) {
        return toResponse(requireArmy(armyId));
    }

    private Army requireArmy(String armyId) {
        Army army = armyRepositoryPort.findById(armyId);
        if (army == null) {
            throw new IllegalArgumentException("Ejército no encontrado");
        }
        return army;
    }

    private ArmyResponse toResponse(Army army) {
        List<UnitResponse> unitResponses = new ArrayList<>();
        for (Unit unit : army.getUnits()) {
            unitResponses.add(new UnitResponse(unit.getType(), unit.getPoints(), unit.getTrainingCount()));
        }
        return new ArmyResponse(army.getId(), army.getCivilization(), army.getGold(), unitResponses, List.copyOf(army.getBattleHistory()));
    }
}
