package com.kala.military.domain;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Aggregate root of the simulation: an army of a given civilization, its gold and its units.
 *
 * <p>The army protects its own invariants defensively: every public operation validates its input
 * and throws {@link IllegalArgumentException} on a business rule violation, regardless of which
 * adapter invoked it.
 */
public final class Army {

    private static final Logger logger = LoggerFactory.getLogger(Army.class);

    private static final int INITIAL_GOLD = 1000;
    private static final int TRANSFORMATION_COST = 30;
    private static final int BATTLE_REWARD = 100;
    private static final int BATTLE_PENALTY = 50;

    private static final Map<String, Integer> TRAINING_COSTS = Map.of(
            "Piquero", 30,
            "Arquero", 40,
            "Caballero", 50);
    private static final Map<String, Integer> BASE_POINTS = Map.of(
            "Piquero", 5,
            "Arquero", 8,
            "Caballero", 12);

    @NonNull
    private final String id;

    @NonNull
    private final String civilization;

    private int gold;

    @NonNull
    private final List<Unit> units = new ArrayList<>();

    @NonNull
    private final List<String> battleHistory = new ArrayList<>();

    private Army(@Nullable String civilization) {
        this.id = UUID.randomUUID().toString();
        this.civilization = normalizeCivilization(civilization);
        this.gold = INITIAL_GOLD;
        initializeUnits(this.civilization);
    }

    /**
     * Creates an army with the initial gold and units of the given civilization.
     *
     * @param civilization civilization name in English or Spanish (e.g. {@code china}, {@code chinos})
     * @return a new army with {@value #INITIAL_GOLD} gold and its starting units
     * @throws IllegalArgumentException if the civilization is blank or unsupported
     */
    @NonNull
    public static Army of(@Nullable String civilization) {
        return new Army(civilization);
    }

    /** Gold rewarded to the winner of a battle. */
    public static int battleReward() {
        return BATTLE_REWARD;
    }

    /** Gold taken from the loser of a battle. */
    public static int battlePenalty() {
        return BATTLE_PENALTY;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getCivilization() {
        return civilization;
    }

    public int getGold() {
        return gold;
    }

    /**
     * Subtracts gold, never going below zero.
     *
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void spendGold(int amount) {
        if (amount < 0) {
            logger.warn("Negative gold spend attempted for army {}", id);
            throw new IllegalArgumentException("El gasto no puede ser negativo");
        }
        this.gold = Math.max(this.gold - amount, 0);
        logger.info("Army {} spent {} gold. Remaining gold: {}", id, amount, this.gold);
    }

    /**
     * Adds gold to the army.
     *
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void earnGold(int amount) {
        if (amount < 0) {
            logger.warn("Negative gold gain attempted for army {}", id);
            throw new IllegalArgumentException("La ganancia no puede ser negativa");
        }
        this.gold += amount;
        logger.info("Army {} earned {} gold. Total gold: {}", id, amount, this.gold);
    }

    /**
     * Returns an unmodifiable view of the units. The army's composition can only be changed through
     * its own operations.
     */
    @NonNull
    public List<Unit> getUnits() {
        return List.copyOf(units);
    }

    /** Returns an unmodifiable view of the recorded battle results. */
    @NonNull
    public List<String> getBattleHistory() {
        return List.copyOf(battleHistory);
    }

    public void addBattleResult(@Nullable String result) {
        battleHistory.add(requireText(result, "El resultado de la batalla es obligatorio"));
        logger.info("Battle result recorded for army {}: {}", id, result);
    }

    /**
     * Returns the gold required to train the given unit type, or {@code 0} if the type is unknown.
     *
     * @throws IllegalArgumentException if {@code unitType} is blank
     */
    public int trainingCost(@Nullable String unitType) {
        return TRAINING_COSTS.getOrDefault(requireUnitType(unitType), 0);
    }

    /**
     * Returns the gold required to transform {@code sourceType} into {@code targetType}.
     *
     * @throws IllegalArgumentException if the transformation is not part of the supported cycle
     */
    public int transformationCost(@Nullable String sourceType, @Nullable String targetType) {
        if (!canTransform(requireUnitType(sourceType), requireUnitType(targetType))) {
            throw new IllegalArgumentException("Regla de transformación no soportada");
        }
        return TRANSFORMATION_COST;
    }

    /**
     * Trains an existing unit, increasing its points.
     *
     * @throws IllegalArgumentException if the army has no unit of that type
     */
    public void trainUnit(@Nullable String unitType) {
        var unit = findUnitByType(requireUnitType(unitType));
        if (unit == null) {
            logger.warn("Training failed for army {} because unit {} was not found", id, unitType);
            throw new IllegalArgumentException("Unidad no encontrada");
        }
        unit.train();
        logger.info("Army {} trained unit {}", id, unitType);
    }

    /**
     * Replaces a unit of {@code sourceType} with one of {@code targetType}, keeping the highest of
     * its current points and the target type's base points. The training count is reset.
     *
     * @throws IllegalArgumentException if the transformation is unsupported or the unit is missing
     */
    public void transformUnit(@Nullable String sourceType, @Nullable String targetType) {
        var source = requireUnitType(sourceType);
        var target = requireUnitType(targetType);
        if (!canTransform(source, target)) {
            logger.warn("Transformation rule unsupported for army {}: {} -> {}", id, source, target);
            throw new IllegalArgumentException("Regla de transformación no soportada");
        }

        var index = findUnitIndex(source);
        if (index < 0) throw new IllegalArgumentException("Unidad no encontrada");

        var existingUnit = units.get(index);
        var targetPoints = Math.max(BASE_POINTS.getOrDefault(target, existingUnit.getPoints()), existingUnit.getPoints());
        units.set(index, new Unit(target, targetPoints));
        logger.info("Army {} transformed unit {} to {}", id, source, target);
    }

    /** Sum of the points of every unit; the value compared in a battle. */
    public int calculateTotalPoints() {
        return units.stream().mapToInt(Unit::getPoints).sum();
    }

    public boolean hasUnitType(@Nullable String unitType) {
        return findUnitByType(requireUnitType(unitType)) != null;
    }

    /** Removes the unit with the fewest points. Does nothing when the army has no units left. */
    public void removeWeakestUnit() {
        if (units.isEmpty()) {
            logger.info("Army {} has no units to remove", id);
            return;
        }

        var weakestIndex = 0;
        var weakestPoints = units.get(0).getPoints();
        for (var index = 1; index < units.size(); index++) {
            var candidate = units.get(index);
            if (candidate.getPoints() < weakestPoints) {
                weakestIndex = index;
                weakestPoints = candidate.getPoints();
            }
        }
        units.remove(weakestIndex);
        logger.info("Army {} removed weakest unit", id);
    }

    @Nullable
    private Unit findUnitByType(@NonNull String unitType) {
        for (var unit : units) {
            if (unitType.equals(unit.getType())) return unit;
        }
        return null;
    }

    private int findUnitIndex(@NonNull String unitType) {
        for (var index = 0; index < units.size(); index++) {
            if (unitType.equals(units.get(index).getType())) return index;
        }
        return -1;
    }

    private boolean canTransform(@NonNull String sourceType, @NonNull String targetType) {
        return switch (sourceType) {
            case "Piquero" -> "Arquero".equals(targetType);
            case "Arquero" -> "Caballero".equals(targetType);
            case "Caballero" -> "Piquero".equals(targetType);
            default -> false;
        };
    }

    private void initializeUnits(@NonNull String civilization) {
        switch (civilization) {
            case "china" -> {
                units.add(new Unit("Piquero", 5));
                units.add(new Unit("Arquero", 8));
                units.add(new Unit("Caballero", 12));
            }
            case "english" -> {
                units.add(new Unit("Piquero", 5));
                units.add(new Unit("Arquero", 8));
                units.add(new Unit("Caballero", 12));
            }
            case "byzantine" -> {
                units.add(new Unit("Piquero", 5));
                units.add(new Unit("Arquero", 8));
                units.add(new Unit("Caballero", 12));
            }
            default -> throw new IllegalArgumentException("Civilización no soportada");
        }
    }

    @NonNull
    private static String normalizeCivilization(@Nullable String civilization) {
        var normalized = requireText(civilization, "La civilización es obligatoria").trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "china", "chinos" -> "china";
            case "english", "ingleses" -> "english";
            case "byzantine", "bizantinos" -> "byzantine";
            default -> throw new IllegalArgumentException("Civilización no soportada");
        };
    }

    @NonNull
    private static String requireUnitType(@Nullable String unitType) {
        return requireText(unitType, "El tipo de unidad es obligatorio");
    }

    @NonNull
    private static String requireText(@Nullable String value, @NonNull String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value;
    }
}
