package com.kala.military.application.dto;

import jakarta.validation.constraints.NotBlank;

/** Request to simulate a battle between two armies. */
public record BattleRequest(
        @NotBlank(message = "El identificador del primer ejército es obligatorio") String firstArmyId,
        @NotBlank(message = "El identificador del segundo ejército es obligatorio") String secondArmyId) {
}
