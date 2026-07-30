package com.kala.military.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnitTest {

    @Test
    void shouldIncreasePointsWhenTraining() {
        Unit unit = new Unit("Piquero", 5);

        unit.train();

        assertEquals(10, unit.getPoints());
        assertEquals(1, unit.getTrainingCount());
    }
}
