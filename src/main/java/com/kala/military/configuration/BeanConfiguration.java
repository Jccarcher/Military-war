package com.kala.military.configuration;

import com.kala.military.adapters.out.inmemory.InMemoryArmyRepository;
import com.kala.military.application.ports.out.ArmyRepositoryPort;
import com.kala.military.application.services.ArmyApplicationService;
import com.kala.military.application.services.BattleApplicationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(BeanConfiguration.class);

    @Bean
    public ArmyRepositoryPort armyRepositoryPort() {
        LOGGER.info("Creating in-memory army repository bean");
        return new InMemoryArmyRepository();
    }

    @Bean
    public ArmyApplicationService armyApplicationService(ArmyRepositoryPort armyRepositoryPort) {
        LOGGER.info("Creating army application service bean");
        return new ArmyApplicationService(armyRepositoryPort);
    }

    @Bean
    public BattleApplicationService battleApplicationService(ArmyRepositoryPort armyRepositoryPort) {
        LOGGER.info("Creating battle application service bean");
        return new BattleApplicationService(armyRepositoryPort);
    }
}
