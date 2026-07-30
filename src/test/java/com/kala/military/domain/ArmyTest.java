package com.kala.military.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ArmyTest {

    @Test
    void shouldCreateArmyWithInitialStateBasedOnCivilization() {
        var army = Army.of("china");

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
        var army = Army.of("ingleses");

        assertEquals("english", army.getCivilization());
    }

    @Test
    void shouldRejectUnsupportedCivilization() {
        assertThrows(IllegalArgumentException.class, () -> Army.of("romanos"));
    }

    @Test
    void shouldRejectBlankCivilization() {
        var exception = assertThrows(IllegalArgumentException.class, () -> Army.of("  "));

        assertEquals("La civilización es obligatoria", exception.getMessage());
    }

    @Test
    void shouldSpendAndEarnGoldWithoutGoingBelowZero() {
        var army = Army.of("china");

        army.spendGold(1500);
        assertEquals(0, army.getGold());

        army.earnGold(250);
        assertEquals(250, army.getGold());
    }

    @Test
    void shouldRejectNegativeGoldMovements() {
        var army = Army.of("byzantine");

        assertThrows(IllegalArgumentException.class, () -> army.spendGold(-1));
        assertThrows(IllegalArgumentException.class, () -> army.earnGold(-1));
    }

    @Test
    void shouldRejectBlankUnitType() {
        var army = Army.of("china");

        assertThrows(IllegalArgumentException.class, () -> army.trainUnit(null));
        assertThrows(IllegalArgumentException.class, () -> army.hasUnitType(""));
        assertThrows(IllegalArgumentException.class, () -> army.transformUnit(null, "Arquero"));
    }

    @Test
    void shouldRejectUnsupportedTransformation() {
        var army = Army.of("china");

        assertThrows(IllegalArgumentException.class, () -> army.transformUnit("Piquero", "Caballero"));
    }

    @Test
    void shouldNotExposeItsUnitsForExternalMutation() {
        var army = Army.of("china");

        assertThrows(UnsupportedOperationException.class, () -> army.getUnits().clear());
        assertEquals(3, army.getUnits().size());
    }

    @Test
    void shouldRejectTransformationFromAnUnknownUnitType() {
        var army = Army.of("china");

        var exception = assertThrows(IllegalArgumentException.class, () -> army.transformUnit("Mago", "Arquero"));

        assertEquals("Regla de transformación no soportada", exception.getMessage());
    }

    @Test
    void shouldRejectTransformationWhenTheSourceUnitIsGone() {
        var army = Army.of("china");
        army.transformUnit("Piquero", "Arquero");

        var exception = assertThrows(IllegalArgumentException.class, () -> army.transformUnit("Piquero", "Arquero"));

        assertEquals("Unidad no encontrada", exception.getMessage());
    }

    @Test
    void shouldRejectTrainingAUnitTheArmyDoesNotHave() {
        var army = Army.of("china");
        army.transformUnit("Piquero", "Arquero");

        var exception = assertThrows(IllegalArgumentException.class, () -> army.trainUnit("Piquero"));

        assertEquals("Unidad no encontrada", exception.getMessage());
    }

    @Test
    void shouldSupportTheWholeTransformationCycle() {
        var army = Army.of("china");

        army.transformUnit("Arquero", "Caballero");
        assertEquals("Caballero", army.getUnits().get(1).getType());

        army.transformUnit("Caballero", "Piquero");
        assertEquals("Piquero", army.getUnits().get(1).getType());
        assertEquals(12, army.getUnits().get(1).getPoints());
    }

    @Test
    void shouldPriceOnlySupportedTransformations() {
        var army = Army.of("china");

        assertEquals(30, army.transformationCost("Piquero", "Arquero"));
        assertEquals(30, army.transformationCost("Arquero", "Caballero"));
        assertEquals(30, army.transformationCost("Caballero", "Piquero"));

        var exception = assertThrows(IllegalArgumentException.class,
                () -> army.transformationCost("Piquero", "Caballero"));
        assertEquals("Regla de transformación no soportada", exception.getMessage());
    }

    @Test
    void shouldRemoveTheWeakestUnitEvenWhenItIsNotTheFirstOne() {
        var army = Army.of("china");
        army.trainUnit("Piquero");
        army.trainUnit("Piquero");

        army.removeWeakestUnit();

        assertEquals(2, army.getUnits().size());
        assertEquals("Piquero", army.getUnits().get(0).getType());
        assertEquals("Caballero", army.getUnits().get(1).getType());
    }

    @Test
    void shouldReportZeroTrainingCostForAnUnknownUnitType() {
        var army = Army.of("china");

        assertEquals(0, army.trainingCost("Mago"));
        assertEquals(30, army.trainingCost("Piquero"));
        assertEquals(40, army.trainingCost("Arquero"));
        assertEquals(50, army.trainingCost("Caballero"));
    }

    @Test
    void shouldExposeTheBattleGoldRules() {
        assertEquals(100, Army.battleReward());
        assertEquals(50, Army.battlePenalty());
    }

    @Test
    void shouldRecordBattleResultsAndRejectBlankOnes() {
        var army = Army.of("china");

        army.addBattleResult("Battle vs rival: victory");

        assertEquals(1, army.getBattleHistory().size());
        assertThrows(IllegalArgumentException.class, () -> army.addBattleResult("  "));
        assertThrows(UnsupportedOperationException.class, () -> army.getBattleHistory().clear());
    }

    @Test
    void shouldRemoveTheWeakestUnitAndTolerateAnEmptyArmy() {
        var army = Army.of("china");

        army.removeWeakestUnit();
        assertEquals("Arquero", army.getUnits().get(0).getType());

        army.removeWeakestUnit();
        army.removeWeakestUnit();
        assertEquals(0, army.getUnits().size());

        army.removeWeakestUnit();
        assertEquals(0, army.getUnits().size());
        assertEquals(0, army.calculateTotalPoints());
    }

    @Test
    void shouldReportWhetherItHasAUnitType() {
        var army = Army.of("china");

        assertTrue(army.hasUnitType("Caballero"));
        assertFalse(army.hasUnitType("Mago"));
    }

    @Test
    void shouldKeepHighestPointsWhenTransforming() {
        var army = Army.of("china");
        army.trainUnit("Piquero");
        army.trainUnit("Piquero");

        army.transformUnit("Piquero", "Arquero");

        assertEquals(15, army.getUnits().get(0).getPoints());
        assertEquals("Arquero", army.getUnits().get(0).getType());
    }
}
