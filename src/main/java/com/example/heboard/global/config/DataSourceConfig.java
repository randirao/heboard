package com.example.heboard.global.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private final Environment environment;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String resolvedUrl = resolveDatabaseUrl(properties.getUrl(), properties);
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

    private String resolveDatabaseUrl(String defaultUrl, DataSourceProperties properties) {
        String externalUrl = environment.getProperty("EXTERNAL_DATABASE_URL");
        String internalUrl = environment.getProperty("INTERNAL_DATABASE_URL");

        String candidate = firstNonEmpty(externalUrl, internalUrl, defaultUrl);
        if (!StringUtils.hasText(candidate)) {
            return defaultUrl;
        }

        return normalizeJdbcUrl(candidate, defaultUrl, properties);
    }

    private String normalizeJdbcUrl(String candidate, String defaultUrl, DataSourceProperties properties) {
        try {
            // remove optional jdbc: prefix and normalize scheme
            String withoutJdbc = candidate.replaceFirst("^jdbc:", "");
            String normalized = withoutJdbc.replaceFirst("^postgres://", "postgresql://");

            URI uri = URI.create(normalized);

            // extract user info if present
            String userInfo = uri.getUserInfo();
            if (StringUtils.hasText(userInfo)) {
                String[] parts = userInfo.split(":", 2);
                if (parts.length > 0 && StringUtils.hasText(parts[0]) && !StringUtils.hasText(properties.getUsername())) {
                    properties.setUsername(parts[0]);
                }
                if (parts.length > 1 && StringUtils.hasText(parts[1]) && !StringUtils.hasText(properties.getPassword())) {
                    properties.setPassword(parts[1]);
                }
            }

            String host = uri.getHost();
            int port = uri.getPort();
            String path = uri.getPath();
            String query = uri.getQuery();

            if (!StringUtils.hasText(host)) {
                return ensureSsl(defaultUrl);
            }

            StringBuilder jdbc = new StringBuilder("jdbc:postgresql://").append(host);
            if (port != -1) {
                jdbc.append(":").append(port);
            }
            if (StringUtils.hasText(path)) {
                jdbc.append(path);
            }

            String finalQuery = appendSslMode(query);
            if (StringUtils.hasText(finalQuery)) {
                jdbc.append("?").append(finalQuery);
            }
            return jdbc.toString();
        } catch (Exception e) {
            return ensureSsl(candidate);
        }
    }

    private String appendSslMode(String query) {
        if (!StringUtils.hasText(query)) {
            return "sslmode=require";
        }
        if (query.contains("sslmode=")) {
            return query;
        }
        return query + "&sslmode=require";
    }

    private String ensureSsl(String url) {
        if (!StringUtils.hasText(url)) {
            return url;
        }
        if (url.contains("sslmode=")) {
            return url;
        }
        if (url.contains("?")) {
            return url + "&sslmode=require";
        }
        return url + "?sslmode=require";
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
