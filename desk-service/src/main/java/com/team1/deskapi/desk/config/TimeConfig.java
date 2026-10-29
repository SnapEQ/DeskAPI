package com.team1.deskapi.desk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.validation.autoconfigure.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    @Bean
    Clock clock(@Value("${desk.time-zone}") ZoneId timeZone) {
        return Clock.system(timeZone);
    }

    @Bean
    ValidationConfigurationCustomizer validationClockCustomizer(Clock clock) {
        return configuration -> configuration.clockProvider(() -> clock);
    }
}
