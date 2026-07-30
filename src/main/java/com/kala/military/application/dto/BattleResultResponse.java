package com.kala.military.application.dto;

import org.jspecify.annotations.Nullable;

/**
 * Outcome of a battle, always from the perspective of the first army.
 *
 * @param result   {@code victory}, {@code defeat} or {@code draw}
 * @param winnerId id of the winning army, or {@code null} on a draw
 * @param loserId  id of the losing army, or {@code null} on a draw
 * @param summary  human-readable description of the simulation
 */
public record BattleResultResponse(
        String result,
        @Nullable String winnerId,
        @Nullable String loserId,
        String summary) {
}
