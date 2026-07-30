package com.kala.military.application;

import com.kala.military.application.dto.ArmyResponse;
import com.kala.military.application.dto.CreateArmyRequest;
import com.kala.military.application.dto.TrainUnitRequest;
import com.kala.military.application.dto.TransformUnitRequest;
import com.kala.military.domain.Army;
import com.kala.military.ports.out.ArmyRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArmyServiceTest {

    private ArmyApplicationService service;
    private InMemoryArmyRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArmyRepository();
        service = new ArmyApplicationService(repository);
    }

    @Test
    void shouldCreateArmyWithInitialState() {
        ArmyResponse response = service.createArmy(new CreateArmyRequest("chinos"));

        assertEquals("china", response.civilization());
        assertEquals(1000, response.gold());
        assertEquals(3, response.units().size());
    }

    @Test
    void shouldTrainUnitWhenEnoughGoldAndUnitExists() {
        Army army = Army.create("china");
        repository.save(army);

        ArmyResponse response = service.trainUnit(new TrainUnitRequest(army.getId(), "Piquero"));

        assertEquals(970, response.gold());
        assertTrue(response.units().stream().anyMatch(unit -> "Piquero".equals(unit.type())
                && unit.points() == 10
                && unit.trainingCount() == 1));
    }

    @Test
    void shouldRejectTrainingWhenGoldIsInsufficient() {
        Army army = Army.create("china");
        army.spendGold(1000);
        repository.save(army);

        assertThrows(IllegalArgumentException.class,
                () -> service.trainUnit(new TrainUnitRequest(army.getId(), "Piquero")));
    }

    @Test
    void shouldTransformUnitWhenRuleExists() {
        Army army = Army.create("china");
        repository.save(army);

        ArmyResponse response = service.transformUnit(new TransformUnitRequest(army.getId(), "Piquero", "Arquero"));

        assertEquals(970, response.gold());
        assertTrue(response.units().stream().anyMatch(unit -> "Arquero".equals(unit.type())
                && unit.points() >= 8));
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
