package com.kala.military.application;

import com.kala.military.application.dto.BattleRequest;
import com.kala.military.application.services.BattleApplicationService;
import com.kala.military.application.dto.BattleResultResponse;
import com.kala.military.domain.Army;
import com.kala.military.domain.Unit;
import com.kala.military.ports.out.ArmyRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BattleServiceTest {

    private BattleApplicationService service;
    private InMemoryArmyRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArmyRepository();
        service = new BattleApplicationService(repository);
    }

    @Test
    void shouldDeclareVictoryWhenFirstArmyHasHigherTotalPoints() {
        Army firstArmy = Army.create("china");
        Army secondArmy = Army.create("english");
        firstArmy.getUnits().get(0).train();
        repository.save(firstArmy);
        repository.save(secondArmy);

        BattleResultResponse response = service.simulateBattle(new BattleRequest(firstArmy.getId(), secondArmy.getId()));

        assertEquals(firstArmy.getId(), response.winnerId());
        assertNotNull(response.result());
    }

    @Test
    void shouldReturnDrawWhenBothArmiesHaveSameTotalPoints() {
        Army firstArmy = Army.create("china");
        Army secondArmy = Army.create("english");
        firstArmy.getUnits().clear();
        secondArmy.getUnits().clear();
        firstArmy.getUnits().add(new Unit("Piquero", 5));
        secondArmy.getUnits().add(new Unit("Piquero", 5));
        repository.save(firstArmy);
        repository.save(secondArmy);

        BattleResultResponse response = service.simulateBattle(new BattleRequest(firstArmy.getId(), secondArmy.getId()));

        assertEquals("draw", response.result());
    }

    private static final class InMemoryArmyRepository implements ArmyRepositoryPort {

        private final Map<String, Army> armies = new HashMap<>();

        @Override
        public Army save(Army army) {
            armies.put(army.getId(), army);
            return army;
        }

        @Override
        public Army findById(String id) {
            return armies.get(id);
        }
    }
}
