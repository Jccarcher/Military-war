package com.kala.military.application.services;

import com.kala.military.application.dto.ArmyResponse;
import com.kala.military.application.dto.CreateArmyRequest;
import com.kala.military.application.dto.TrainUnitRequest;
import com.kala.military.application.dto.TransformUnitRequest;
import com.kala.military.application.dto.UnitResponse;
import com.kala.military.application.ports.in.ArmyUseCasePort;
import com.kala.military.application.ports.out.ArmyRepositoryPort;
import com.kala.military.domain.Army;
import com.kala.military.domain.Unit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public final class ArmyApplicationService implements ArmyUseCasePort {

    private static final Logger LOGGER = LogManager.getLogger(ArmyApplicationService.class);

    private final ArmyRepositoryPort armyRepositoryPort;

    public ArmyApplicationService(ArmyRepositoryPort armyRepositoryPort) {
        this.armyRepositoryPort = armyRepositoryPort;
    }

    public ArmyResponse createArmy(CreateArmyRequest request) {
        LOGGER.info("Creating army for civilization {}", request.civilization());
        Army army = Army.create(request.civilization());
        armyRepositoryPort.save(army);
        LOGGER.info("Army {} created successfully", army.getId());
        return toResponse(army);
    }

    public ArmyResponse trainUnit(TrainUnitRequest request) {
        LOGGER.info("Training unit {} for army {}", request.unitType(), request.armyId());
        Army army = requireArmy(request.armyId());
        if (!army.hasUnitType(request.unitType())) {
            LOGGER.warn("Training rejected because unit {} was not found in army {}", request.unitType(), request.armyId());
            throw new IllegalArgumentException("Unidad no encontrada");
        }

        int trainingCost = army.getTrainingCost(request.unitType());
        if (army.getGold() < trainingCost) {
            LOGGER.warn("Training rejected for army {} due to insufficient gold", request.armyId());
            throw new IllegalArgumentException("Oro insuficiente");
        }

        army.spendGold(trainingCost);
        army.trainUnit(request.unitType());
        armyRepositoryPort.save(army);
        LOGGER.info("Training completed for unit {} in army {}", request.unitType(), request.armyId());
        return toResponse(army);
    }

    public ArmyResponse transformUnit(TransformUnitRequest request) {
        LOGGER.info("Transforming unit {} to {} for army {}", request.sourceType(), request.targetType(), request.armyId());
        Army army = requireArmy(request.armyId());
        int transformationCost = army.getTransformationCost(request.sourceType(), request.targetType());
        if (army.getGold() < transformationCost) {
            LOGGER.warn("Transformation rejected for army {} due to insufficient gold", request.armyId());
            throw new IllegalArgumentException("Oro insuficiente");
        }

        army.spendGold(transformationCost);
        army.transformUnit(request.sourceType(), request.targetType());
        armyRepositoryPort.save(army);
        LOGGER.info("Transformation completed for army {}", request.armyId());
        return toResponse(army);
    }

    public ArmyResponse getArmy(String armyId) {
        LOGGER.info("Retrieving army {}", armyId);
        return toResponse(requireArmy(armyId));
    }

    private Army requireArmy(String armyId) {
        Army army = armyRepositoryPort.findById(armyId);
        if (army == null) {
            LOGGER.error("Army {} was requested but was not found", armyId);
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
