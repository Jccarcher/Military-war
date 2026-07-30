package com.kala.military.application.ports.in;

import com.kala.military.application.dto.BattleRequest;
import com.kala.military.application.dto.BattleResultResponse;

import org.jspecify.annotations.NonNull;

/** Inbound port exposing the battle simulation use case. */
public interface BattleUseCasePort {

    /**
     * Compares the total points of both armies and applies the outcome to each of them.
     *
     * @throws IllegalArgumentException if either army does not exist
     */
    @NonNull
    BattleResultResponse simulateBattle(@NonNull BattleRequest request);
}
