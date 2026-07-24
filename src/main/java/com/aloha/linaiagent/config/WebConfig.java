package com.aloha.linaiagent.config;

import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web-related configuration.
 */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class WebConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    public WebConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var mapping = registry.addMapping(corsProperties.getPathPattern());

        List<String> allowedOrigins = corsProperties.getAllowedOrigins();
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            mapping.allowedOrigins(allowedOrigins.toArray(new String[0]));
        }

        List<String> allowedMethods = corsProperties.getAllowedMethods();
        if (allowedMethods != null && !allowedMethods.isEmpty()) {
            mapping.allowedMethods(allowedMethods.toArray(new String[0]));
        }

        List<String> allowedHeaders = corsProperties.getAllowedHeaders();
        if (allowedHeaders != null && !allowedHeaders.isEmpty()) {
            mapping.allowedHeaders(allowedHeaders.toArray(new String[0]));
        }

        List<String> exposedHeaders = corsProperties.getExposedHeaders();
        if (exposedHeaders != null && !exposedHeaders.isEmpty()) {
            mapping.exposedHeaders(exposedHeaders.toArray(new String[0]));
        }

        mapping.allowCredentials(corsProperties.isAllowCredentials());
        mapping.maxAge(corsProperties.getMaxAge());
    }
}
