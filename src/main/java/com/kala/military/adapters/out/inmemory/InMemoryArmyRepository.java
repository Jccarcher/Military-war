package com.kala.military.adapters.out.inmemory;

import com.kala.military.application.ports.out.ArmyRepositoryPort;
import com.kala.military.domain.Army;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Outbound adapter that keeps the armies in the process memory. State is lost on restart.
 *
 * <p>Backed by a {@link ConcurrentHashMap} so concurrent requests cannot corrupt the map itself.
 */
public final class InMemoryArmyRepository implements ArmyRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryArmyRepository.class);

    @NonNull
    private final Map<String, Army> armies = new ConcurrentHashMap<>();

    @Override
    @NonNull
    public Army save(@NonNull Army army) {
        logger.info("Saving army {} into memory repository", army.getId());
        armies.put(army.getId(), army);
        return army;
    }

    @Override
    @Nullable
    public Army findById(@Nullable String id) {
        if (id == null) return null;

        var army = armies.get(id);
        if (army == null) {
            logger.warn("Army {} not found in memory repository", id);
        } else {
            logger.info("Army {} retrieved from memory repository", id);
        }
        return army;
    }
}
