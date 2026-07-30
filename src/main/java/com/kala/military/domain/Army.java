package com.kala.military.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class Army {
    private static final Logger LOGGER = LogManager.getLogger(Army.class);

    private static final int INITIAL_GOLD = 1000;
    private static final Map<String, Integer> TRAINING_COSTS = Map.of(
            "Piquero", 30,
            "Arquero", 40,
            "Caballero", 50);
    private static final Map<String, Integer> BASE_POINTS = Map.of(
            "Piquero", 5,
            "Arquero", 8,
            "Caballero", 12);

    private final String id;
    private final String civilization;
    private int gold;
    private final List<Unit> units = new ArrayList<>();
    private final List<String> battleHistory = new ArrayList<>();

    private Army(String civilization) {
        this.id = UUID.randomUUID().toString();
        this.civilization = normalizeCivilization(civilization);
        this.gold = INITIAL_GOLD;
        initializeUnits(this.civilization);
    }

    public static Army create(String civilization) {
        return new Army(civilization);
    }

    public String getId() {
        return id;
    }

    public String getCivilization() {
        return civilization;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = Math.max(gold, 0);
    }

    public void spendGold(int amount) {
        if (amount < 0) {
            LOGGER.warn("Negative gold spend attempted for army {}", id);
            throw new IllegalArgumentException("El gasto no puede ser negativo");
        }
        this.gold = Math.max(this.gold - amount, 0);
        LOGGER.info("Army {} spent {} gold. Remaining gold: {}", id, amount, this.gold);
    }

    public void earnGold(int amount) {
        if (amount < 0) {
            LOGGER.warn("Negative gold gain attempted for army {}", id);
            throw new IllegalArgumentException("La ganancia no puede ser negativa");
        }
        this.gold += amount;
        LOGGER.info("Army {} earned {} gold. Total gold: {}", id, amount, this.gold);
    }

    public List<Unit> getUnits() {
        return units;
    }

    public List<String> getBattleHistory() {
        return battleHistory;
    }

    public void addBattleResult(String result) {
        battleHistory.add(result);
        LOGGER.info("Battle result recorded for army {}: {}", id, result);
    }

    public int getTrainingCost(String unitType) {
        return TRAINING_COSTS.getOrDefault(unitType, 0);
    }

    public int getTransformationCost(String sourceType, String targetType) {
        if (!canTransform(sourceType, targetType)) {
            throw new IllegalArgumentException("Regla de transformación no soportada");
        }
        return 30;
    }

    public void trainUnit(String unitType) {
        Unit unit = findUnitByType(unitType);
        if (unit == null) {
            LOGGER.warn("Training failed for army {} because unit {} was not found", id, unitType);
            throw new IllegalArgumentException("Unidad no encontrada");
        }
        unit.train();
        LOGGER.info("Army {} trained unit {}", id, unitType);
    }

    public void transformUnit(String sourceType, String targetType) {
        if (!canTransform(sourceType, targetType)) {
            LOGGER.warn("Transformation rule unsupported for army {}: {} -> {}", id, sourceType, targetType);
            throw new IllegalArgumentException("Regla de transformación no soportada");
        }

        int index = findUnitIndex(sourceType);
        if (index < 0) {
            throw new IllegalArgumentException("Unidad no encontrada");
        }

        Unit existingUnit = units.get(index);
        int targetPoints = Math.max(BASE_POINTS.getOrDefault(targetType, existingUnit.getPoints()), existingUnit.getPoints());
        units.set(index, new Unit(targetType, targetPoints));
        LOGGER.info("Army {} transformed unit {} to {}", id, sourceType, targetType);
    }

    public int calculateTotalPoints() {
        return units.stream().mapToInt(Unit::getPoints).sum();
    }

    public boolean hasUnitType(String unitType) {
        return findUnitByType(unitType) != null;
    }

    public void removeWeakestUnit() {
        if (units.isEmpty()) {
            LOGGER.info("Army {} has no units to remove", id);
            return;
        }

        int weakestIndex = 0;
        int weakestPoints = units.get(0).getPoints();
        for (int index = 1; index < units.size(); index++) {
            Unit candidate = units.get(index);
            if (candidate.getPoints() < weakestPoints) {
                weakestIndex = index;
                weakestPoints = candidate.getPoints();
            }
        }
        units.remove(weakestIndex);
        LOGGER.info("Army {} removed weakest unit", id);
    }

    private Unit findUnitByType(String unitType) {
        for (Unit unit : units) {
            if (unitType.equals(unit.getType())) {
                return unit;
            }
        }
        return null;
    }

    private int findUnitIndex(String unitType) {
        for (int index = 0; index < units.size(); index++) {
            if (unitType.equals(units.get(index).getType())) {
                return index;
            }
        }
        return -1;
    }

    private boolean canTransform(String sourceType, String targetType) {
        return switch (sourceType) {
            case "Piquero" -> "Arquero".equals(targetType);
            case "Arquero" -> "Caballero".equals(targetType);
            case "Caballero" -> "Piquero".equals(targetType);
            default -> false;
        };
    }

    private void initializeUnits(String civilization) {
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

    private static String normalizeCivilization(String civilization) {
        if (civilization == null || civilization.isBlank()) {
            throw new IllegalArgumentException("La civilización es obligatoria");
        }

        String normalized = civilization.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "china", "chinos" -> "china";
            case "english", "ingleses" -> "english";
            case "byzantine", "bizantinos" -> "byzantine";
            default -> throw new IllegalArgumentException("Civilización no soportada");
        };
    }
}
