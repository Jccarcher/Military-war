package com.kala.military.configuration;

import com.kala.military.adapters.out.inmemory.InMemoryArmyRepository;
import com.kala.military.application.ports.in.ArmyUseCasePort;
import com.kala.military.application.ports.in.BattleUseCasePort;
import com.kala.military.application.ports.out.ArmyRepositoryPort;
import com.kala.military.application.services.ArmyApplicationService;
import com.kala.military.application.services.BattleApplicationService;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the hexagon: the domain and application layers stay free of Spring, so their beans are
 * declared here and always exposed through their port type.
 */
@Configuration(proxyBeanMethods = false)
public final class BeanConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(BeanConfiguration.class);

    @Bean
    @NonNull
    public ArmyRepositoryPort armyRepositoryPort() {
        logger.info("Creating in-memory army repository bean");
        return new InMemoryArmyRepository();
    }

    @Bean
    @NonNull
    public ArmyUseCasePort armyApplicationService(@NonNull ArmyRepositoryPort armyRepositoryPort) {
        logger.info("Creating army application service bean");
        return new ArmyApplicationService(armyRepositoryPort);
    }

    @Bean
    @NonNull
    public BattleUseCasePort battleApplicationService(@NonNull ArmyRepositoryPort armyRepositoryPort) {
        logger.info("Creating battle application service bean");
        return new BattleApplicationService(armyRepositoryPort);
    }
}
