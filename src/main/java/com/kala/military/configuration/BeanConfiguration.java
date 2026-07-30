package com.kala.military.configuration;

import com.kala.military.adapters.out.inmemory.InMemoryArmyRepository;
import com.kala.military.application.services.ArmyApplicationService;
import com.kala.military.application.services.BattleApplicationService;
import com.kala.military.ports.out.ArmyRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ArmyRepositoryPort armyRepositoryPort() {
        return new InMemoryArmyRepository();
    }

    @Bean
    public ArmyApplicationService armyApplicationService(ArmyRepositoryPort armyRepositoryPort) {
        return new ArmyApplicationService(armyRepositoryPort);
    }

    @Bean
    public BattleApplicationService battleApplicationService(ArmyRepositoryPort armyRepositoryPort) {
        return new BattleApplicationService(armyRepositoryPort);
    }
}
