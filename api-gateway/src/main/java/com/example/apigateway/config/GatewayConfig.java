package com.example.apigateway.config;

import com.example.apigateway.service.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter filter;

    public GatewayConfig(JwtAuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()

                //  Auth Service: PUBLIC (login/register)
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service")) // utilise eureka pour service discovery (load balancing)

                //  Expense Service: PROTÉGÉ
                .route("expense-service", r -> r.path("/api/expenses/**")
                        .filters(f -> f.filter(filter)) //application du filtre jwt
                        .uri("lb://expense-service"))

                // Budget Service: PROTÉGÉ
                .route("budget-service", r -> r.path("/api/budgets/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://budget-service"))

                // Agent IA Service: PROTÉGÉ
                .route("agent-ia-service", r -> r.path("/api/chat/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://agent-ia-service"))

                //  MCP endpoints: PUBLIC
                .route("mcp-expense", r -> r.path("/expense-service/mcp/**")
                        .filters(f -> f.rewritePath(
                                "/expense-service/mcp/(?<segment>.*)",
                                "/mcp/${segment}")) // rewriting of URL before forwarding
                        .uri("lb://expense-service"))

                .route("mcp-budget", r -> r.path("/budget-service/mcp/**")
                        .filters(f -> f.rewritePath("/budget-service/mcp/(?<segment>.*)", "/mcp/${segment}"))
                        .uri("lb://budget-service"))

                // Actuator: PUBLIC
                .route("actuator", r -> r.path("/actuator/**")
                        .uri("lb://auth-service"))

                .build();
    }
}