package com.kala.military.adapters.out.inmemory;

import com.kala.military.domain.Army;
import com.kala.military.ports.out.ArmyRepositoryPort;

import java.util.HashMap;
import java.util.Map;

public class InMemoryArmyRepository implements ArmyRepositoryPort {

    private final Map<String, Army> armies = new HashMap<>();

    @Override
    public Army save(Army army) {
        armies.put(army.getId(), army);
        return army;
    }

    @Override
    public Army findById(String id) {
        return armies.get(id);
    }
}
