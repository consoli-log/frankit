package com.soli.frankit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * packageName  : com.soli.frankit.config
 * fileName     : SwaggerConfig
 * author       : eumsoli
 * date         : 2025-02-24
 * description  : Swagger 설정
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정을 정의하는 Bean
     *
     * @return OpenAPI 객체
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("프랜킷 API")
                        .description("프랜킷 API 명세서")
                        .version("v1.0"))
                .servers(List.of(new Server().url("http://localhost:8080").description("로컬 서버")
                ));
    }

}
