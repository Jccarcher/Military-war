package com.kala.military.application.dto;

import jakarta.validation.constraints.NotBlank;

import org.jspecify.annotations.Nullable;

/**
 * Request to train a unit.
 *
 * @param armyId   taken from the request path, not from the request body; {@code null} while the
 *                 REST adapter has not merged it yet
 * @param unitType type of the unit to train
 */
public record TrainUnitRequest(
        @Nullable String armyId,
        @NotBlank(message = "El tipo de unidad es obligatorio") String unitType) {
}
