package com.company.demo.config;

import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
@ConfigurationPropertiesScan
public class WebConfig implements WebMvcConfigurer {

    private static final String FORWARD_INDEX = "forward:/index.html";

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    @Primary
    CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(
                Arrays.asList("x-requested-with", "Content-Type", "Authorization", "credential", "X-XSRF-TOKEN"));
        corsConfig.setAllowCredentials(false);
        corsConfig.addAllowedOrigin("*");
        corsConfig.setAllowedMethods(Arrays.asList(HttpMethod.OPTIONS.name(), HttpMethod.HEAD.name(),
                HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name(),
                HttpMethod.PATCH.name()));
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfig);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName(FORWARD_INDEX);
        registry.addViewController("/{x:[\\w\\-]+}").setViewName(FORWARD_INDEX);
        registry.addViewController("/{x:^(?!api$).*$}/**/{y:[\\w\\-]+}").setViewName(FORWARD_INDEX);
    }
}
