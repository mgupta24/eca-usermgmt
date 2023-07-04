package com.eca.usermgmt.config;

import brave.sampler.Sampler;
import com.eca.usermgmt.service.strategy.UserFetchStrategy;
import com.eca.usermgmt.service.strategy.UserStrategyFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class UserManagementConfig {

    @Value("${app.openapi.dev-url}")
    private String devUrl;

    @Value("${app.openapi.prod-url}")
    private String prodUrl;

    @Bean
    public ObjectMapper objectMapper(){
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public UserStrategyFactory userStrategyFactory(List<UserFetchStrategy> strategyList) {
        var strategyMap = strategyList
                .stream()
                .collect(Collectors.toMap(UserFetchStrategy::strategyName, Function.identity()));
        return new UserStrategyFactory(strategyMap);
    }

    @Bean
    public OpenAPI myOpenAPI() {
        var devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        var prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        var contact = new Contact();
        contact.setEmail("eca-academy@gmail.com");
        contact.setName("eca academy");
        contact.setUrl("https://www.eca-academy.com");

        var mitLicense = new License().name("PS License").url("https://eca.com/licenses/ps/");

        var info = new Info().title("ECA User Management API").version("1.0").contact(contact)
                .description("This API exposes endpoints to manage user-management")
                .termsOfService("https://www.eca-academy.com/terms").license(mitLicense);

        return new OpenAPI()
                .info(info).servers(List.of(devServer, prodServer));
    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

}
