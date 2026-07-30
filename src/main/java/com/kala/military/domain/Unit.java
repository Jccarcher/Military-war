package com.kala.military.domain;

import java.util.Objects;

public final class Unit {
    private final String type;
    private int points;
    private int trainingCount;

    public Unit(String type, int points) {
        this(type, points, 0);
    }

    private Unit(String type, int points, int trainingCount) {
        this.type = type;
        this.points = points;
        this.trainingCount = trainingCount;
    }

    public String getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public int getTrainingCount() {
        return trainingCount;
    }

    public void train() {
        this.trainingCount++;
        this.points += 5;
    }

    public void updatePoints(int points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Unit unit)) {
            return false;
        }
        return points == unit.points && trainingCount == unit.trainingCount && Objects.equals(type, unit.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, points, trainingCount);
    }
}
