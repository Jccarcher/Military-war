package com.kala.military.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArmyTest {

    @Test
    void shouldCreateArmyWithInitialStateBasedOnCivilization() {
        Army army = Army.create("china");

        assertNotNull(army);
        assertEquals("china", army.getCivilization());
        assertEquals(1000, army.getGold());
        assertEquals(3, army.getUnits().size());
        assertEquals("Piquero", army.getUnits().get(0).getType());
        assertEquals("Arquero", army.getUnits().get(1).getType());
        assertEquals("Caballero", army.getUnits().get(2).getType());
    }

    @Test
    void shouldNormalizeSupportedCivilizationAliases() {
        Army army = Army.create("ingleses");

        assertEquals("english", army.getCivilization());
    }

    @Test
    void shouldNotAllowNegativeGold() {
        Army army = Army.create("byzantine");
        army.setGold(50);

        assertTrue(army.getGold() >= 0);
    }

    @Test
    void shouldSpendAndEarnGoldWithoutGoingBelowZero() {
        Army army = Army.create("china");

        army.spendGold(1500);
        assertEquals(0, army.getGold());

        army.earnGold(250);
        assertEquals(250, army.getGold());
    }
}
