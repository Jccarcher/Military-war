package com.kala.military.application.ports.in;

import com.kala.military.application.dto.ArmyResponse;
import com.kala.military.application.dto.CreateArmyRequest;
import com.kala.military.application.dto.TrainUnitRequest;
import com.kala.military.application.dto.TransformUnitRequest;

public interface ArmyUseCasePort {

    ArmyResponse createArmy(CreateArmyRequest request);

    ArmyResponse trainUnit(TrainUnitRequest request);

    ArmyResponse transformUnit(TransformUnitRequest request);

    ArmyResponse getArmy(String armyId);
}
