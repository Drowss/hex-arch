package com.hexarch.redis;

import com.hexarch.Product;
import com.hexarch.gateways.ProductCacheGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCacheAdapter implements ProductCacheGateway {

    private final ReactiveRedisTemplate<String, Product> redisTemplate;
    private static final String KEY_PREFIX = "product:";
    private static final Duration TTL = Duration.ofMinutes(10);

    @Override
    public Mono<Product> getFromCache(String id) {
        return redisTemplate.opsForValue()
                .get(KEY_PREFIX + id)
                .doOnNext(p -> log.info("[REDIS] GET hit para key: {}", KEY_PREFIX + id))
                .doOnSubscribe(s -> log.info("[REDIS] GET para key: {}", KEY_PREFIX + id));
    }

    @Override
    public Mono<Product> saveInCache(Product product) {
        String key = KEY_PREFIX + product.getId();
        return redisTemplate.opsForValue()
                .set(key, product, TTL)
                .thenReturn(product)
                .doOnSuccess(p -> log.info("[REDIS] SET OK para key: {} con TTL: {}", key, TTL));
    }
}
