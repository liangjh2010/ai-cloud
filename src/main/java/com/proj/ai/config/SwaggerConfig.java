package com.proj.ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 配置
 */
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Cloud 服务 API")
                        .description("AI Cloud Ollama 调用服务接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("liangjunhui")
                                .email("liangjunhui@example.com"))
                        .license(new License()
                                .name("内部使用")
                                .url("")));
    }
}

