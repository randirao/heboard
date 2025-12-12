package com.example.heboard.global.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
public class DataSourceConfig {

    private final Environment environment;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Primary
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
            return ensureSsl("jdbc:postgresql://" + candidate.substring("postgres://".length()));
        }
        if (candidate.startsWith("postgresql://")) {
            return ensureSsl("jdbc:postgresql://" + candidate.substring("postgresql://".length()));
        }
        if (candidate.startsWith("jdbc:postgresql://")) {
            return ensureSsl(candidate);
        }

        return ensureSsl(candidate);
    }

    private String firstNonEmpty(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String ensureSsl(String url) {
        if (!StringUtils.hasText(url)) {
            return url;
        }
        // sslmode가 이미 포함되어 있으면 그대로 사용
        if (url.contains("sslmode=")) {
            return url;
        }
        // 쿼리스트링 유무에 따라 구분자 추가
        if (url.contains("?")) {
            return url + "&sslmode=require";
        }
        return url + "?sslmode=require";
    }
}
