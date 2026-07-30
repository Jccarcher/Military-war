package com.kala.military.application.dto;

import org.jspecify.annotations.NonNull;

/** State of a single unit as exposed by the API. */
public record UnitResponse(@NonNull String type, int points, int trainingCount) {
}
