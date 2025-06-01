package org.edu.fpm.transportation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for the application
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Add simple view controllers for pages that don't need controller logic
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/access-denied").setViewName("error/access-denied");
    }
}
