package yoon.capstone.application.security;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi memberApiGroup(){
        return GroupedOpenApi.builder()
                .group("MEMBER API")
                .pathsToMatch("/api/v1/members/**")
                .build();
    }

    @Bean
    public GroupedOpenApi apiGroup(){
        return GroupedOpenApi.builder()
                .group("PROJECT API")
                .pathsToMatch("/api/v1/projects/**")
                .build();
    }
}
