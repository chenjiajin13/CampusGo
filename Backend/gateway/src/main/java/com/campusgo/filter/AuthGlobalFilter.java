package com.campusgo.filter;

import com.campusgo.client.AuthClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final ObjectProvider<AuthClient> authClientProvider;
    private final ObjectMapper objectMapper;

    private static final List<String> WHITELIST_PREFIX = List.of(
            "/api/auth/",
            "/v3/api-docs",
            "/swagger-ui",
            "/actuator"
    );
    private static final Pattern PUBLIC_MERCHANT_DETAIL = Pattern.compile("^/api/merchants/\\d+$");
    private static final Pattern PUBLIC_MERCHANT_MENU = Pattern.compile("^/api/merchants/\\d+/menu$");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String path = exchange.getRequest().getURI().getPath();

        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "MISSING_TOKEN");
        }

        String token = authHeader.substring(7);

        return Mono.fromCallable(() -> {
                    AuthClient authClient = authClientProvider.getIfAvailable();
                    if (authClient == null) {
                        throw new IllegalStateException("AuthClient bean unavailable");
                    }
                    Response response = authClient.validate(token);
                    String raw = readBody(response);
                    return parseValidate(raw);
                })
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .flatMap(resp -> {
                    if (resp == null || !resp.valid) {
                        return unauthorized(exchange, resp == null ? "INVALID" : resp.message);
                    }

                    ServerHttpRequest mutated = exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(resp.userId))
                            .header("X-Principal-Type", resp.principalType)
                            .header("X-Token-Exp", String.valueOf(resp.expiresAt))
                            .build();

                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .onErrorResume(e -> unauthorized(exchange, "GW_V2_" + e.getClass().getSimpleName()));
    }

    private boolean isWhitelisted(String path) {
        if ("/api/merchants".equals(path)) {
            return true;
        }
        if (PUBLIC_MERCHANT_DETAIL.matcher(path).matches()) {
            return true;
        }
        if (PUBLIC_MERCHANT_MENU.matcher(path).matches()) {
            return true;
        }
        for (String prefix : WHITELIST_PREFIX) {
            if (path.startsWith(prefix)) return true;
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        byte[] bytes = ("{\"code\":\"UNAUTHORIZED\",\"message\":\"" + msg + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        exchange.getResponse().getHeaders().set("Content-Type", "application/json;charset=UTF-8");
        exchange.getResponse().getHeaders().set("X-Gateway-Version", "gw-auth-map-v2");
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }

    private ParsedValidate parseValidate(String raw) throws Exception {
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("EMPTY_VALIDATE_RESPONSE");
        }
        JsonNode n = objectMapper.readTree(raw);
        ParsedValidate p = new ParsedValidate();
        p.valid = n.path("valid").asBoolean(false);
        p.userId = n.path("userId").asLong(0L);
        p.expiresAt = n.path("expiresAt").asLong(0L);
        p.principalType = n.path("principalType").asText("");
        p.message = n.path("message").asText("INVALID");
        return p;
    }

    private String readBody(Response response) throws Exception {
        if (response == null || response.body() == null) {
            throw new IllegalStateException("EMPTY_VALIDATE_HTTP_BODY");
        }
        try (InputStream in = response.body().asInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static class ParsedValidate {
        boolean valid;
        long userId;
        long expiresAt;
        String principalType;
        String message;
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
