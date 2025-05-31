package org.edu.fpm.transportation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@Profile("classpath:application-${spring.profiles.active}.properties")
public class DataSourceConfig {
    @Value("${db.driver}")
    private String driverName;
    @Value("${db.url}")
    private String dbUrl;
    @Value("${db.username}")
    private String dbUsername;
    @Value("${db.password}")
    private String dbPassword;

    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        return dataSource();
    }

    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        return dataSource();
    }

    @Bean
    @Profile("local")
    public DataSource localDataSource() {
        return dataSource();
    }

    @Bean
    @Profile("stg")
    public DataSource stgDataSource() {
        return dataSource();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverName);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }
}
