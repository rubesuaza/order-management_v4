package com.example.order_management.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for JPA persistence layer.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.order_management.infrastructure.persistence.repository")
@EntityScan(basePackages = "com.example.order_management.infrastructure.persistence.entity")
@EnableTransactionManagement
public class PersistenceConfig {
    // Configuration handled by annotations
}
