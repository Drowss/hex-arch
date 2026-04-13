package com.hexarch.api.product;

import com.hexarch.Product;
import com.hexarch.usecase.ProductCacheUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductCacheUseCase productCacheUseCase;

    public Mono<ServerResponse> cacheAside(ServerRequest request) {
        String id = request.pathVariable("id");
        return productCacheUseCase.getProductCacheAside(id)
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(e -> ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> writeThrough(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .flatMap(productCacheUseCase::saveProductWriteThrough)
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    public Mono<ServerResponse> writeBehind(ServerRequest request) {
        return request.bodyToMono(Product.class)
                .flatMap(productCacheUseCase::saveProductWriteBehind)
                .flatMap(product -> ServerResponse.ok().bodyValue(product))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}
