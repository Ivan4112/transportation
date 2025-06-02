package org.edu.fpm.transportation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        Hibernate5JakartaModule hibernate5Module = new Hibernate5JakartaModule();
        // Configure Hibernate5Module to handle lazy loading properly
        hibernate5Module.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, false);
        hibernate5Module.configure(Hibernate5JakartaModule.Feature.USE_TRANSIENT_ANNOTATION, true);
        
        // Add JavaTimeModule to handle java.time.* classes like Instant
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        return Jackson2ObjectMapperBuilder.json()
                .modules(hibernate5Module, javaTimeModule)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
}
