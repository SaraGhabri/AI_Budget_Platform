package com.example.apigateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    /*
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Public endpoints (ajoutés)
        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        // Check header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        // Validate token
        try {
            jwtUtil.validateToken(token);
        } catch (Exception e) {
            return unauthorized(exchange);
        }

        //  Extract roles from token
        List<String> roles = jwtUtil.extractRoles(token);
        boolean isAdmin = roles.contains("ROLE_ADMIN");
        boolean isUser  = roles.contains("ROLE_USER");

        //  Check permissions
        if (path.startsWith("/ask")) {
            if (isUser || isAdmin) {
                return chain.filter(exchange);
            }
            return forbidden(exchange);
        }

        //
        if (path.startsWith("/api/expenses") || path.startsWith("/api/budgets")) {
            if (isAdmin) return chain.filter(exchange);
            return forbidden(exchange);
        }

        // ✅ Pour /api/expenses et /api/budgets, juste passer avec headers
        return chain.filter(exchange);
    }*/
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Public endpoints = skip authentification
        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        // extraction du jwt de auth header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7); //to remove bearer

        // valider token
        try {
            jwtUtil.validateToken(token);
        } catch (Exception e) {
            return unauthorized(exchange);
        }

        // Extract roles from JWT claims
        List<String> roles = jwtUtil.extractRoles(token);

        // Check for both formats: with and without ROLE_ prefix
        boolean isAdmin = roles.stream().anyMatch(r ->
                r.equals("ADMIN") || r.equals("ROLE_ADMIN"));
        boolean isUser = roles.stream().anyMatch(r ->
                r.equals("USER") || r.equals("ROLE_USER"));

        System.out.println("DEBUG - Path: " + path);
        System.out.println("DEBUG - Roles: " + roles);
        System.out.println("DEBUG - isAdmin: " + isAdmin + ", isUser: " + isUser);

        // Authorization logic for /api/budgets and /api/expenses = pour admin et user
        if (path.startsWith("/api/budgets") || path.startsWith("/api/expenses")) {
            if (isAdmin || isUser) {
                // ✅ Extract username and add headers
                String username = jwtUtil.extractUsername(token);
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", username)
                        .header("X-User-Roles", String.join(",", roles))
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }
            return forbidden(exchange);
        }

        // Authorization for /ask (AI chat)
        if (path.startsWith("/ask")) {
            if (isUser || isAdmin) {
                return chain.filter(exchange);
            }
            return forbidden(exchange);
        }

        return chain.filter(exchange);
    }
    //Define public routes (no authentication needed)
    private boolean isPublicRoute(String path) {
        return path.equals("/api/login") ||
                path.startsWith("/api/auth/") ||  // AJOUTÉ
                path.startsWith("/actuator") ||
                path.contains("/mcp/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }
}