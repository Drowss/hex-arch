package com.hexarch.usecase;

import com.hexarch.Product;
import com.hexarch.gateways.ProductCacheGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
public class ProductCacheUseCase {

    private final ProductCacheGateway cacheGateway;

    private static final List<Product> FAKE_DB = List.of(
            Product.builder().id("1").name("Laptop").price(999.99).build(),
            Product.builder().id("2").name("Mouse").price(29.99).build(),
            Product.builder().id("3").name("Teclado").price(59.99).build()
    );

    private Mono<Product> findInFakeDb(String id) {
        return Mono.justOrEmpty(
                FAKE_DB.stream().filter(p -> p.getId().equals(id)).findFirst()
        ).delayElement(Duration.ofSeconds(5));
    }

    // 1. CACHE ASIDE (Lazy Loading)
    public Mono<Product> getProductCacheAside(String id) {
        return cacheGateway.getFromCache(id)
                .switchIfEmpty(
                        Mono.defer(() -> findInFakeDb(id)
                                .flatMap(cacheGateway::saveInCache))
                )
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado con id: " + id)));
    }

    // 2. WRITE THROUGH
    public Mono<Product> saveProductWriteThrough(Product product) {
        return Mono.zip(saveInFakeDb(product), cacheGateway.saveInCache(product))
                .map(Tuple2::getT1);
    }

    // 3. WRITE BEHIND (Write Back)
    public Mono<Product> saveProductWriteBehind(Product product) {
        return cacheGateway.saveInCache(product)
                .doOnSuccess(cached ->
                        saveInFakeDb(cached).subscribe()
                );
    }

    private Mono<Product> saveInFakeDb(Product product) {
        return Mono.just(product);
    }
}
