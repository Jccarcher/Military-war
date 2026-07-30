package com.kala.military.application.ports.out;

import com.kala.military.domain.Army;

public interface ArmyRepositoryPort {
    Army save(Army army);

    Army findById(String id);
}
