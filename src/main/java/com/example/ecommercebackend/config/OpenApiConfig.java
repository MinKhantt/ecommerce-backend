package com.example.ecommercebackend.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Min Khant Munag",
                        email = "minkhantmaung2558@gmail.com",
                        url = "https://min-khant-maung.netlify.app/"
                ),
                title = "E-Commerce API",
                description = "RESTful API for an e-commerce platform with product catalog management, shopping cart, order processing, payment integration (Stripe), user authentication (JWT & OAuth2) and image management via Cloudinary.",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:9000"
                ),
                @Server(
                        description = "PROD ENV",
                        url = "http://localhost:9000"
                )
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT access token obtained from /api/v1/auth/login",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
