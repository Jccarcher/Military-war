package com.kala.military.application;

import com.kala.military.application.ports.out.ArmyRepositoryPort;
import com.kala.military.domain.Army;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Test double for {@link ArmyRepositoryPort}. Lets the application tests run without pulling in the
 * outbound adapter, keeping the hexagon boundaries intact in tests too.
 */
final class FakeArmyRepository implements ArmyRepositoryPort {

    private final Map<String, Army> armies = new HashMap<>();

    @Override
    @NonNull
    public Army save(@NonNull Army army) {
        armies.put(army.getId(), army);
        return army;
    }

    @Override
    @Nullable
    public Army findById(@Nullable String id) {
        return id == null ? null : armies.get(id);
    }
}
