package com.kala.military.application;

import com.kala.military.application.dto.BattleRequest;
import com.kala.military.application.services.BattleApplicationService;
import com.kala.military.domain.Army;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class BattleServiceTest {

    private BattleApplicationService service;
    private FakeArmyRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FakeArmyRepository();
        service = new BattleApplicationService(repository);
    }

    @Test
    void shouldDeclareVictoryWhenFirstArmyHasHigherTotalPoints() {
        var firstArmy = Army.of("china");
        var secondArmy = Army.of("english");
        firstArmy.trainUnit("Piquero");
        repository.save(firstArmy);
        repository.save(secondArmy);

        var response = service.simulateBattle(new BattleRequest(firstArmy.getId(), secondArmy.getId()));

        assertEquals("victory", response.result());
        assertEquals(firstArmy.getId(), response.winnerId());
        assertEquals(secondArmy.getId(), response.loserId());
        assertNotNull(response.summary());
    }

    @Test
    void shouldReturnDrawWhenBothArmiesHaveSameTotalPoints() {
        var firstArmy = Army.of("china");
        var secondArmy = Army.of("english");
        repository.save(firstArmy);
        repository.save(secondArmy);

        var response = service.simulateBattle(new BattleRequest(firstArmy.getId(), secondArmy.getId()));

        assertEquals("draw", response.result());
        assertNull(response.winnerId());
        assertNull(response.loserId());
    }

    @Test
    void shouldRemoveWeakestUnitFromBothArmiesOnDraw() {
        var firstArmy = Army.of("china");
        var secondArmy = Army.of("english");
        repository.save(firstArmy);
        repository.save(secondArmy);

        service.simulateBattle(new BattleRequest(firstArmy.getId(), secondArmy.getId()));

        assertEquals(2, firstArmy.getUnits().size());
        assertEquals(2, secondArmy.getUnits().size());
    }

    @Test
    void shouldDeclareDefeatWhenSecondArmyHasHigherTotalPoints() {
        var firstArmy = Army.of("china");
        var secondArmy = Army.of("english");
        secondArmy.trainUnit("Caballero");
        repository.save(firstArmy);
        repository.save(secondArmy);

        var response = service.simulateBattle(new BattleRequest(firstArmy.getId(), secondArmy.getId()));

        assertEquals("defeat", response.result());
        assertEquals(secondArmy.getId(), response.winnerId());
        assertEquals(firstArmy.getId(), response.loserId());
        assertEquals(1100, secondArmy.getGold());
        assertEquals(950, firstArmy.getGold());
    }

    @Test
    void shouldRecordTheOutcomeInBothBattleHistories() {
        var firstArmy = Army.of("china");
        var secondArmy = Army.of("english");
        repository.save(firstArmy);
        repository.save(secondArmy);

        service.simulateBattle(new BattleRequest(firstArmy.getId(), secondArmy.getId()));

        assertEquals(1, firstArmy.getBattleHistory().size());
        assertEquals("Battle vs " + secondArmy.getId() + ": draw", firstArmy.getBattleHistory().get(0));
        assertEquals("Battle vs " + firstArmy.getId() + ": draw", secondArmy.getBattleHistory().get(0));
    }

    @Test
    void shouldRejectBattlesReferencingUnknownArmies() {
        var army = Army.of("china");
        repository.save(army);

        assertThrows(IllegalArgumentException.class,
                () -> service.simulateBattle(new BattleRequest("missing-id", army.getId())));
        assertThrows(IllegalArgumentException.class,
                () -> service.simulateBattle(new BattleRequest(army.getId(), "missing-id")));
    }

    @Test
    void shouldRewardWinnerAndPenalizeLoser() {
        var firstArmy = Army.of("china");
        var secondArmy = Army.of("english");
        firstArmy.trainUnit("Piquero");
        repository.save(firstArmy);
        repository.save(secondArmy);

        service.simulateBattle(new BattleRequest(firstArmy.getId(), secondArmy.getId()));

        assertEquals(1100, firstArmy.getGold());
        assertEquals(950, secondArmy.getGold());
        assertEquals(2, secondArmy.getUnits().size());
    }
}
