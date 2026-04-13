package com.hexarch.gateways;

import com.hexarch.Product;
import reactor.core.publisher.Mono;

public interface ProductCacheGateway {
    Mono<Product> getFromCache(String id);
    Mono<Product> saveInCache(Product product);
}