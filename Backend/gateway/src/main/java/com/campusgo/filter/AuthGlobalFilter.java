package com.campusgo.filter;

import com.campusgo.client.AuthClient;
import com.campusgo.dto.ValidateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthClient authClient;

    // 白名单：不需要鉴权
    private static final List<String> WHITELIST_PREFIX = List.of(
            "/api/auth/",          // 登录/注册/refresh/logout
            "/v3/api-docs",        // swagger
            "/swagger-ui",         // swagger
            "/actuator"            // actuator
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "MISSING_TOKEN");
        }

        String token = authHeader.substring(7);

        // ⚠️ Feign 是阻塞的，Gateway 是 WebFlux 非阻塞。
        // 成熟做法：用 WebClient；但为了你能先跑通，这里用 boundedElastic 包一下阻塞调用。
        return Mono.fromCallable(() -> authClient.validate(token))
                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                .flatMap(resp -> {
                    if (resp == null || !resp.isValid()) {
                        return unauthorized(exchange, resp == null ? "INVALID" : resp.getMessage());
                    }

                    ServerHttpRequest mutated = exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(resp.getUserId()))
                            .header("X-Principal-Type", resp.getPrincipalType() == null ? "" : resp.getPrincipalType())
                            .header("X-Token-Exp", String.valueOf(resp.getExpiresAt()))
                            .build();

                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .onErrorResume(e -> unauthorized(exchange, e.getClass().getSimpleName()));
    }

    private boolean isWhitelisted(String path) {
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
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -100; // 越小越先执行
    }
}
