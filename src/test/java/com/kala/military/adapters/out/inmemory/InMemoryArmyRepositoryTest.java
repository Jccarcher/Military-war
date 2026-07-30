package com.kala.military.adapters.out.inmemory;

import com.kala.military.domain.Army;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

final class InMemoryArmyRepositoryTest {

    private InMemoryArmyRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArmyRepository();
    }

    @Test
    void shouldReturnTheSavedArmy() {
        var army = Army.of("china");

        var saved = repository.save(army);

        assertSame(army, saved);
    }

    @Test
    void shouldFindAPreviouslySavedArmyById() {
        var army = Army.of("china");
        repository.save(army);

        var found = repository.findById(army.getId());

        assertSame(army, found);
    }

    @Test
    void shouldReturnNullWhenTheArmyIsUnknown() {
        assertNull(repository.findById("missing-id"));
    }

    @Test
    void shouldReturnNullWhenTheIdIsNull() {
        assertNull(repository.findById(null));
    }

    @Test
    void shouldReplaceTheStateHeldUnderTheSameId() {
        var army = Army.of("china");
        repository.save(army);

        army.spendGold(100);
        repository.save(army);

        var found = repository.findById(army.getId());
        assertEquals(900, found.getGold());
    }
}
