package com.bidding.platform.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        contact=@Contact(
            name="Umesh Kumar",
            email="ukm3423@gmail.com",
            url="https://devyntra.com"
        ),
        description="OpenApi Documentation for Spring Security",
        title="OpenApi Specification - Umesh Kumar",
        version="1.0",
        license=@License(
            name="Apache License, Version 2.0",
            url="http://www.apache.org/licenses/LICENSE"
        ),
        termsOfService="Terms of Service"
    ),
    servers={
        @Server(
            url="http://localhost:8080",
            description="Local Server"
        ),
        @Server(
            url="https://10.163.75.4:8080",
            description="Production Server"
        ),
        @Server(
            url="https://your-api-server.com",
            description="Production Server"
        )
    },
    security={
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    in = SecuritySchemeIn.HEADER,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT Auth Token"
)
public class OpenApiConfig {
    
}