package com.example.heboard.global.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
public class DataSourceConfig {

    private final Environment environment;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        String resolvedUrl = resolveDatabaseUrl(properties.getUrl());
        if (StringUtils.hasText(resolvedUrl)) {
            properties.setUrl(resolvedUrl);
        }

        if (!StringUtils.hasText(properties.getUsername())) {
            properties.setUsername(environment.getProperty("DB_USER"));
        }
        if (!StringUtils.hasText(properties.getPassword())) {
            properties.setPassword(environment.getProperty("DB_PASSWORD"));
        }

        return properties.initializeDataSourceBuilder().build();
    }

    private String resolveDatabaseUrl(String defaultUrl) {
        String externalUrl = environment.getProperty("EXTERNAL_DATABASE_URL");
        String internalUrl = environment.getProperty("INTERNAL_DATABASE_URL");

        String candidate = firstNonEmpty(externalUrl, internalUrl, defaultUrl);
        if (!StringUtils.hasText(candidate)) {
            return defaultUrl;
        }

        if (candidate.startsWith("postgres://")) {
            return "jdbc:postgresql://" + candidate.substring("postgres://".length());
        }
        if (candidate.startsWith("postgresql://")) {
            return "jdbc:postgresql://" + candidate.substring("postgresql://".length());
        }
        if (candidate.startsWith("jdbc:postgresql://")) {
            return candidate;
        }

        return candidate;
    }

    private String firstNonEmpty(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }
}
