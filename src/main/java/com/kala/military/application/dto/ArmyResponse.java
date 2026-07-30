package com.kala.military.application.dto;

import org.jspecify.annotations.NonNull;

import java.util.List;

/** Current state of an army as exposed by the API. */
public record ArmyResponse(
        @NonNull String id,
        @NonNull String civilization,
        int gold,
        @NonNull List<UnitResponse> units,
        @NonNull List<String> battleHistory) {
}
