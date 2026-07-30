package com.kala.military.application.ports.in;

import com.kala.military.application.dto.ArmyResponse;
import com.kala.military.application.dto.CreateArmyRequest;
import com.kala.military.application.dto.TrainUnitRequest;
import com.kala.military.application.dto.TransformUnitRequest;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Inbound port exposing the army use cases.
 *
 * <p>Every operation throws {@link IllegalArgumentException} when a business rule is violated:
 * unknown army or unit, unsupported civilization or transformation, or insufficient gold.
 */
public interface ArmyUseCasePort {

    @NonNull
    ArmyResponse createArmy(@NonNull CreateArmyRequest request);

    @NonNull
    ArmyResponse trainUnit(@NonNull TrainUnitRequest request);

    @NonNull
    ArmyResponse transformUnit(@NonNull TransformUnitRequest request);

    @NonNull
    ArmyResponse getArmy(@Nullable String armyId);
}
