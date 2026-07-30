package com.kala.military.application.services;

import com.kala.military.application.dto.BattleRequest;
import com.kala.military.application.dto.BattleResultResponse;
import com.kala.military.application.ports.in.BattleUseCasePort;
import com.kala.military.application.ports.out.ArmyRepositoryPort;
import com.kala.military.domain.Army;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class BattleApplicationService implements BattleUseCasePort {

    private static final Logger LOGGER = LogManager.getLogger(BattleApplicationService.class);

    private final ArmyRepositoryPort armyRepositoryPort;

    public BattleApplicationService(ArmyRepositoryPort armyRepositoryPort) {
        this.armyRepositoryPort = armyRepositoryPort;
    }

    public BattleResultResponse simulateBattle(BattleRequest request) {
        LOGGER.info("Starting battle simulation between {} and {}", request.firstArmyId(), request.secondArmyId());
        Army firstArmy = requireArmy(request.firstArmyId());
        Army secondArmy = requireArmy(request.secondArmyId());

        int firstPoints = firstArmy.calculateTotalPoints();
        int secondPoints = secondArmy.calculateTotalPoints();

        String result;
        String winnerId = null;
        String loserId = null;

        if (firstPoints > secondPoints) {
            LOGGER.info("Battle result: victory for {}", firstArmy.getId());
            result = "victory";
            winnerId = firstArmy.getId();
            loserId = secondArmy.getId();
            applyBattleOutcome(firstArmy, secondArmy);
        } else if (secondPoints > firstPoints) {
            LOGGER.info("Battle result: defeat for {}", firstArmy.getId());
            result = "defeat";
            winnerId = secondArmy.getId();
            loserId = firstArmy.getId();
            applyBattleOutcome(secondArmy, firstArmy);
        } else {
            LOGGER.warn("Battle resulted in a draw between {} and {}", firstArmy.getId(), secondArmy.getId());
            result = "draw";
            firstArmy.removeWeakestUnit();
            secondArmy.removeWeakestUnit();
        }

        firstArmy.addBattleResult("Battle vs " + secondArmy.getId() + ": " + result);
        secondArmy.addBattleResult("Battle vs " + firstArmy.getId() + ": " + result);

        armyRepositoryPort.save(firstArmy);
        armyRepositoryPort.save(secondArmy);

        LOGGER.info("Battle simulation completed for {} and {} with result {}", firstArmy.getId(), secondArmy.getId(), result);
        return new BattleResultResponse(result, winnerId, loserId, "Battle simulated successfully");
    }

    private void applyBattleOutcome(Army winner, Army loser) {
        winner.earnGold(100);
        loser.removeWeakestUnit();
        loser.spendGold(50);
    }

    private Army requireArmy(String armyId) {
        Army army = armyRepositoryPort.findById(armyId);
        if (army == null) {
            LOGGER.error("Battle simulation referenced unknown army {}", armyId);
            throw new IllegalArgumentException("Ejército no encontrado");
        }
        return army;
    }
}
