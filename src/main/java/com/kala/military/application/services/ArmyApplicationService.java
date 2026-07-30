package com.kala.military.application.services;

import com.kala.military.application.dto.ArmyResponse;
import com.kala.military.application.dto.CreateArmyRequest;
import com.kala.military.application.dto.TrainUnitRequest;
import com.kala.military.application.dto.TransformUnitRequest;
import com.kala.military.application.dto.UnitResponse;
import com.kala.military.application.ports.in.ArmyUseCasePort;
import com.kala.military.application.ports.out.ArmyRepositoryPort;
import com.kala.military.domain.Army;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/** Orchestrates the army use cases on top of the domain and the repository port. */
public final class ArmyApplicationService implements ArmyUseCasePort {

    private static final Logger logger = LoggerFactory.getLogger(ArmyApplicationService.class);

    @NonNull
    private final ArmyRepositoryPort armyRepositoryPort;

    public ArmyApplicationService(@NonNull ArmyRepositoryPort armyRepositoryPort) {
        this.armyRepositoryPort = armyRepositoryPort;
    }

    @Override
    @NonNull
    public ArmyResponse createArmy(@NonNull CreateArmyRequest request) {
        logger.info("Creating army for civilization {}", request.civilization());
        var army = Army.of(request.civilization());
        armyRepositoryPort.save(army);
        logger.info("Army {} created successfully", army.getId());
        return toResponse(army);
    }

    @Override
    @NonNull
    public ArmyResponse trainUnit(@NonNull TrainUnitRequest request) {
        logger.info("Training unit {} for army {}", request.unitType(), request.armyId());
        var army = requireArmy(request.armyId());
        if (!army.hasUnitType(request.unitType())) {
            logger.warn("Training rejected because unit {} was not found in army {}", request.unitType(), request.armyId());
            throw new IllegalArgumentException("Unidad no encontrada");
        }

        var trainingCost = army.trainingCost(request.unitType());
        if (army.getGold() < trainingCost) {
            logger.warn("Training rejected for army {} due to insufficient gold", request.armyId());
            throw new IllegalArgumentException("Oro insuficiente");
        }

        army.spendGold(trainingCost);
        army.trainUnit(request.unitType());
        armyRepositoryPort.save(army);
        logger.info("Training completed for unit {} in army {}", request.unitType(), request.armyId());
        return toResponse(army);
    }

    @Override
    @NonNull
    public ArmyResponse transformUnit(@NonNull TransformUnitRequest request) {
        logger.info("Transforming unit {} to {} for army {}", request.sourceType(), request.targetType(), request.armyId());
        var army = requireArmy(request.armyId());
        var transformationCost = army.transformationCost(request.sourceType(), request.targetType());
        if (army.getGold() < transformationCost) {
            logger.warn("Transformation rejected for army {} due to insufficient gold", request.armyId());
            throw new IllegalArgumentException("Oro insuficiente");
        }

        army.spendGold(transformationCost);
        army.transformUnit(request.sourceType(), request.targetType());
        armyRepositoryPort.save(army);
        logger.info("Transformation completed for army {}", request.armyId());
        return toResponse(army);
    }

    @Override
    @NonNull
    public ArmyResponse getArmy(@Nullable String armyId) {
        logger.info("Retrieving army {}", armyId);
        return toResponse(requireArmy(armyId));
    }

    @NonNull
    private Army requireArmy(@Nullable String armyId) {
        var army = armyRepositoryPort.findById(armyId);
        if (army == null) {
            logger.error("Army {} was requested but was not found", armyId);
            throw new IllegalArgumentException("Ejército no encontrado");
        }
        return army;
    }

    @NonNull
    private ArmyResponse toResponse(@NonNull Army army) {
        List<UnitResponse> unitResponses = new ArrayList<>();
        for (var unit : army.getUnits()) {
            unitResponses.add(new UnitResponse(unit.getType(), unit.getPoints(), unit.getTrainingCount()));
        }
        return new ArmyResponse(army.getId(), army.getCivilization(), army.getGold(), unitResponses, army.getBattleHistory());
    }
}
