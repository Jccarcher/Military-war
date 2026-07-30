package com.kala.military.adapters.out.inmemory;

import com.kala.military.application.ports.out.ArmyRepositoryPort;
import com.kala.military.domain.Army;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class InMemoryArmyRepository implements ArmyRepositoryPort {

    private static final Logger LOGGER = LogManager.getLogger(InMemoryArmyRepository.class);

    private final Map<String, Army> armies = new HashMap<>();

    @Override
    public Army save(Army army) {
        LOGGER.info("Saving army {} into memory repository", army.getId());
        armies.put(army.getId(), army);
        return army;
    }

    @Override
    public Army findById(String id) {
        Army army = armies.get(id);
        if (army == null) {
            LOGGER.warn("Army {} not found in memory repository", id);
        } else {
            LOGGER.info("Army {} retrieved from memory repository", id);
        }
        return army;
    }
}
