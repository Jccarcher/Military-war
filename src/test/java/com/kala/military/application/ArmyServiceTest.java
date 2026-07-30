package com.kala.military.application;

import com.kala.military.application.dto.CreateArmyRequest;
import com.kala.military.application.dto.TrainUnitRequest;
import com.kala.military.application.dto.TransformUnitRequest;
import com.kala.military.application.services.ArmyApplicationService;
import com.kala.military.domain.Army;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ArmyServiceTest {

    private ArmyApplicationService service;
    private FakeArmyRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FakeArmyRepository();
        service = new ArmyApplicationService(repository);
    }

    @Test
    void shouldCreateArmyWithInitialState() {
        var response = service.createArmy(new CreateArmyRequest("chinos"));

        assertEquals("china", response.civilization());
        assertEquals(1000, response.gold());
        assertEquals(3, response.units().size());
    }

    @Test
    void shouldTrainUnitWhenEnoughGoldAndUnitExists() {
        var army = Army.of("china");
        repository.save(army);

        var response = service.trainUnit(new TrainUnitRequest(army.getId(), "Piquero"));

        assertEquals(970, response.gold());
        assertTrue(response.units().stream().anyMatch(unit -> "Piquero".equals(unit.type())
                && unit.points() == 10
                && unit.trainingCount() == 1));
    }

    @Test
    void shouldRejectTrainingWhenGoldIsInsufficient() {
        var army = Army.of("china");
        army.spendGold(1000);
        repository.save(army);

        assertThrows(IllegalArgumentException.class,
                () -> service.trainUnit(new TrainUnitRequest(army.getId(), "Piquero")));
    }

    @Test
    void shouldRejectTrainingWhenUnitTypeIsMissing() {
        var army = Army.of("china");
        repository.save(army);

        var exception = assertThrows(IllegalArgumentException.class,
                () -> service.trainUnit(new TrainUnitRequest(army.getId(), null)));

        assertEquals("El tipo de unidad es obligatorio", exception.getMessage());
    }

    @Test
    void shouldRejectTransformationWhenSourceTypeIsMissing() {
        var army = Army.of("china");
        repository.save(army);

        assertThrows(IllegalArgumentException.class,
                () -> service.transformUnit(new TransformUnitRequest(army.getId(), null, "Arquero")));
    }

    @Test
    void shouldTransformUnitWhenRuleExists() {
        var army = Army.of("china");
        repository.save(army);

        var response = service.transformUnit(new TransformUnitRequest(army.getId(), "Piquero", "Arquero"));

        assertEquals(970, response.gold());
        assertTrue(response.units().stream().anyMatch(unit -> "Arquero".equals(unit.type())
                && unit.points() >= 8));
    }

    @Test
    void shouldRejectTransformationWhenGoldIsInsufficient() {
        var army = Army.of("china");
        army.spendGold(1000);
        repository.save(army);

        var exception = assertThrows(IllegalArgumentException.class,
                () -> service.transformUnit(new TransformUnitRequest(army.getId(), "Piquero", "Arquero")));

        assertEquals("Oro insuficiente", exception.getMessage());
    }

    @Test
    void shouldRejectTrainingAUnitTheArmyDoesNotHave() {
        var army = Army.of("china");
        army.transformUnit("Piquero", "Arquero");
        repository.save(army);

        var exception = assertThrows(IllegalArgumentException.class,
                () -> service.trainUnit(new TrainUnitRequest(army.getId(), "Piquero")));

        assertEquals("Unidad no encontrada", exception.getMessage());
    }

    @Test
    void shouldRejectUnsupportedTransformationBeforeChargingGold() {
        var army = Army.of("china");
        repository.save(army);

        assertThrows(IllegalArgumentException.class,
                () -> service.transformUnit(new TransformUnitRequest(army.getId(), "Piquero", "Caballero")));

        assertEquals(1000, army.getGold());
    }

    @Test
    void shouldReturnTheCurrentStateOfAnExistingArmy() {
        var army = Army.of("bizantinos");
        repository.save(army);

        var response = service.getArmy(army.getId());

        assertEquals(army.getId(), response.id());
        assertEquals("byzantine", response.civilization());
        assertEquals(3, response.units().size());
    }

    @Test
    void shouldRejectUnknownArmy() {
        assertThrows(IllegalArgumentException.class, () -> service.getArmy("missing-id"));
        assertThrows(IllegalArgumentException.class, () -> service.getArmy(null));
    }
}
