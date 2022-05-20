package com.reading.is.good.config;

import com.mongodb.client.MongoClients;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
@Configuration
public class PersistenceConfig {

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/bookorderforcustomers}")
    @Setter
    public String mongoConnectionURI;

    @Value("${spring.data.mongodb.database:bookorderforcustomers}")
    @Setter
    public String databaseName;

    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(MongoClients.create(mongoConnectionURI), databaseName);
    }
}