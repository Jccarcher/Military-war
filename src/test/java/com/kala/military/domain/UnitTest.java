package com.kala.military.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

final class UnitTest {

    @Test
    void shouldIncreasePointsWhenTraining() {
        var unit = new Unit("Piquero", 5);

        unit.train();

        assertEquals(10, unit.getPoints());
        assertEquals(1, unit.getTrainingCount());
    }

    @Test
    void shouldBeEqualToItself() {
        var unit = new Unit("Piquero", 5);

        assertEquals(unit, unit);
    }

    @Test
    void shouldBeEqualToAnotherUnitWithTheSameState() {
        var unit = new Unit("Piquero", 5);
        var sameUnit = new Unit("Piquero", 5);

        assertEquals(unit, sameUnit);
        assertEquals(unit.hashCode(), sameUnit.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenTypeOrPointsDiffer() {
        var unit = new Unit("Piquero", 5);

        assertNotEquals(unit, new Unit("Arquero", 5));
        assertNotEquals(unit, new Unit("Piquero", 8));
    }

    @Test
    void shouldNotBeEqualAfterTraining() {
        var unit = new Unit("Piquero", 5);
        var trainedUnit = new Unit("Piquero", 5);

        trainedUnit.train();

        assertNotEquals(unit, trainedUnit);
    }

    @Test
    void shouldNotBeEqualWhenOnlyTheTrainingCountDiffers() {
        var trainedUnit = new Unit("Piquero", 5);
        trainedUnit.train();

        assertNotEquals(trainedUnit, new Unit("Piquero", 10));
    }

    @Test
    void shouldNotBeEqualToNullOrAnotherType() {
        var unit = new Unit("Piquero", 5);

        assertNotEquals(unit, null);
        assertNotEquals(unit, "Piquero");
    }
}
