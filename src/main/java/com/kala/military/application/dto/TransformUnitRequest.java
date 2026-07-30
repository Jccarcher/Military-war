package com.kala.military.application.dto;

import jakarta.validation.constraints.NotBlank;

import org.jspecify.annotations.Nullable;

/**
 * Request to transform a unit into another type.
 *
 * @param armyId     taken from the request path, not from the request body; {@code null} while the
 *                   REST adapter has not merged it yet
 * @param sourceType type of the unit being transformed
 * @param targetType type the unit is transformed into
 */
public record TransformUnitRequest(
        @Nullable String armyId,
        @NotBlank(message = "El tipo de unidad de origen es obligatorio") String sourceType,
        @NotBlank(message = "El tipo de unidad de destino es obligatorio") String targetType) {
}
