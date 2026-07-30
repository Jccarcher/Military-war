package com.kala.military.domain;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/** A single unit of an army, identified by its type and worth a number of battle points. */
public final class Unit {

    private static final int POINTS_PER_TRAINING = 5;

    @NonNull
    private final String type;

    private int points;

    private int trainingCount;

    public Unit(@NonNull String type, int points) {
        this(type, points, 0);
    }

    private Unit(@NonNull String type, int points, int trainingCount) {
        this.type = type;
        this.points = points;
        this.trainingCount = trainingCount;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public int getPoints() {
        return points;
    }

    public int getTrainingCount() {
        return trainingCount;
    }

    /** Trains the unit, adding {@value #POINTS_PER_TRAINING} points and counting the session. */
    public void train() {
        this.trainingCount++;
        this.points += POINTS_PER_TRAINING;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Unit unit)) return false;
        return points == unit.points && trainingCount == unit.trainingCount && Objects.equals(type, unit.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, points, trainingCount);
    }
}
