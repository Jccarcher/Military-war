package com.kala.military.application.services;

import com.kala.military.application.dto.BattleRequest;
import com.kala.military.application.dto.BattleResultResponse;
import com.kala.military.application.ports.in.BattleUseCasePort;
import com.kala.military.domain.Army;
import com.kala.military.ports.out.ArmyRepositoryPort;

public final class BattleApplicationService implements BattleUseCasePort {

    private final ArmyRepositoryPort armyRepositoryPort;

    public BattleApplicationService(ArmyRepositoryPort armyRepositoryPort) {
        this.armyRepositoryPort = armyRepositoryPort;
    }

    public BattleResultResponse simulateBattle(BattleRequest request) {
        Army firstArmy = requireArmy(request.firstArmyId());
        Army secondArmy = requireArmy(request.secondArmyId());

        int firstPoints = firstArmy.calculateTotalPoints();
        int secondPoints = secondArmy.calculateTotalPoints();

        String result;
        String winnerId = null;
        String loserId = null;

        if (firstPoints > secondPoints) {
            result = "victory";
            winnerId = firstArmy.getId();
            loserId = secondArmy.getId();
            applyBattleOutcome(firstArmy, secondArmy);
        } else if (secondPoints > firstPoints) {
            result = "defeat";
            winnerId = secondArmy.getId();
            loserId = firstArmy.getId();
            applyBattleOutcome(secondArmy, firstArmy);
        } else {
            result = "draw";
            firstArmy.removeWeakestUnit();
            secondArmy.removeWeakestUnit();
        }

        firstArmy.addBattleResult("Battle vs " + secondArmy.getId() + ": " + result);
        secondArmy.addBattleResult("Battle vs " + firstArmy.getId() + ": " + result);

        armyRepositoryPort.save(firstArmy);
        armyRepositoryPort.save(secondArmy);

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
            throw new IllegalArgumentException("Ejército no encontrado");
        }
        return army;
    }
}
