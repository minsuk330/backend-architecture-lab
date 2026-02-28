package com.backend.lab.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("Kmorgan Backend"))
        .security(
            List.of(
                new SecurityRequirement().addList("bearerAuth"),
                new SecurityRequirement().addList("adminSessionHeader")
            )
        )
        .schemaRequirement("bearerAuth", new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization")
        )
        .schemaRequirement("adminSessionHeader", new SecurityScheme()
            .type(Type.APIKEY)
            .in(In.HEADER)
            .name("X-KMORGAN-SID")
        )
        .servers(List.of(
            new Server().url("http://localhost:8080").description("local"),
            new Server().url("https://api.kmorgan.co.kr").description("production")
        ));
  }

}
