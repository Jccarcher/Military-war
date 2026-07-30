package com.kala.military.adapters.in.rest;

import com.kala.military.application.ports.in.ArmyUseCasePort;
import com.kala.military.application.ports.in.BattleUseCasePort;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Checks that the hexagon is wired end to end. The HTTP contract itself is covered by {@link ArmyControllerWebMvcTest}. */
@SpringBootTest
final class ArmyControllerIntegrationTest {

    @Autowired
    private ArmyController armyController;

    @Autowired
    private ArmyUseCasePort armyApplicationService;

    @Autowired
    private BattleUseCasePort battleApplicationService;

    @Test
    void shouldWireRestControllerAndServices() {
        assertNotNull(armyController);
        assertNotNull(armyApplicationService);
        assertNotNull(battleApplicationService);
    }
}
