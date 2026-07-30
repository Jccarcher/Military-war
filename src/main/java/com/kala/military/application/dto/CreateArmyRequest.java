package com.kala.military.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request to create an army.
 *
 * @param civilization civilization name in English or Spanish (e.g. {@code china}, {@code chinos})
 */
public record CreateArmyRequest(@NotBlank(message = "La civilización es obligatoria") String civilization) {
}
