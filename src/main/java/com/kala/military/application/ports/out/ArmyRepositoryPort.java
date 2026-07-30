package com.kala.military.application.ports.out;

import com.kala.military.domain.Army;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Outbound port for army persistence. */
public interface ArmyRepositoryPort {

    /**
     * Stores the army, replacing any previous state held under the same id.
     *
     * @return the saved army
     */
    @NonNull
    Army save(@NonNull Army army);

    /**
     * Looks up an army by id.
     *
     * @return the army, or {@code null} when no army is stored under that id
     */
    @Nullable
    Army findById(@Nullable String id);
}
