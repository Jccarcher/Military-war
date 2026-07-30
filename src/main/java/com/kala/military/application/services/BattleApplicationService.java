package com.kala.military.application.services;

import com.kala.military.application.dto.BattleRequest;
import com.kala.military.application.dto.BattleResultResponse;
import com.kala.military.application.ports.in.BattleUseCasePort;
import com.kala.military.application.ports.out.ArmyRepositoryPort;
import com.kala.military.domain.Army;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Compares two armies and applies the outcome of the battle to both of them. */
public final class BattleApplicationService implements BattleUseCasePort {

    private static final Logger logger = LoggerFactory.getLogger(BattleApplicationService.class);

    private static final String VICTORY = "victory";
    private static final String DEFEAT = "defeat";
    private static final String DRAW = "draw";

    @NonNull
    private final ArmyRepositoryPort armyRepositoryPort;

    public BattleApplicationService(@NonNull ArmyRepositoryPort armyRepositoryPort) {
        this.armyRepositoryPort = armyRepositoryPort;
    }

    @Override
    @NonNull
    public BattleResultResponse simulateBattle(@NonNull BattleRequest request) {
        logger.info("Starting battle simulation between {} and {}", request.firstArmyId(), request.secondArmyId());
        var firstArmy = requireArmy(request.firstArmyId());
        var secondArmy = requireArmy(request.secondArmyId());

        var firstPoints = firstArmy.calculateTotalPoints();
        var secondPoints = secondArmy.calculateTotalPoints();

        String result;
        String winnerId = null;
        String loserId = null;

        if (firstPoints > secondPoints) {
            logger.info("Battle result: victory for {}", firstArmy.getId());
            result = VICTORY;
            winnerId = firstArmy.getId();
            loserId = secondArmy.getId();
            applyBattleOutcome(firstArmy, secondArmy);
        } else if (secondPoints > firstPoints) {
            logger.info("Battle result: defeat for {}", firstArmy.getId());
            result = DEFEAT;
            winnerId = secondArmy.getId();
            loserId = firstArmy.getId();
            applyBattleOutcome(secondArmy, firstArmy);
        } else {
            logger.warn("Battle resulted in a draw between {} and {}", firstArmy.getId(), secondArmy.getId());
            result = DRAW;
            firstArmy.removeWeakestUnit();
            secondArmy.removeWeakestUnit();
        }

        firstArmy.addBattleResult("Battle vs " + secondArmy.getId() + ": " + result);
        secondArmy.addBattleResult("Battle vs " + firstArmy.getId() + ": " + result);

        armyRepositoryPort.save(firstArmy);
        armyRepositoryPort.save(secondArmy);

        logger.info("Battle simulation completed for {} and {} with result {}", firstArmy.getId(), secondArmy.getId(), result);
        return new BattleResultResponse(result, winnerId, loserId, "Battle simulated successfully");
    }

    private void applyBattleOutcome(@NonNull Army winner, @NonNull Army loser) {
        winner.earnGold(Army.battleReward());
        loser.removeWeakestUnit();
        loser.spendGold(Army.battlePenalty());
    }

    @NonNull
    private Army requireArmy(@Nullable String armyId) {
        var army = armyRepositoryPort.findById(armyId);
        if (army == null) {
            logger.error("Battle simulation referenced unknown army {}", armyId);
            throw new IllegalArgumentException("Ejército no encontrado");
        }
        return army;
    }
}
