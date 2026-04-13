package com.hexarch.usecase;

import com.hexarch.Product;
import com.hexarch.gateways.ProductCacheGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductCacheUseCaseTest {

    @Mock
    private ProductCacheGateway cacheGateway;

    @InjectMocks
    private ProductCacheUseCase useCase;

    @Test
    void cacheAside_shouldReturnFromCache_whenCacheHit() {
        Product cached = Product.builder().id("1").name("Laptop").price(999.99).build();
        when(cacheGateway.getFromCache("1")).thenReturn(Mono.just(cached));

        StepVerifier.create(useCase.getProductCacheAside("1"))
                .expectNext(cached)
                .verifyComplete();

        verify(cacheGateway).getFromCache("1");
        verify(cacheGateway, never()).saveInCache(any());
    }

    @Test
    void cacheAside_shouldFetchFromDbAndSaveInCache_whenCacheMiss() {
        Product fromDb = Product.builder().id("1").name("Laptop").price(999.99).build();
        when(cacheGateway.getFromCache("1")).thenReturn(Mono.empty());
        when(cacheGateway.saveInCache(any())).thenReturn(Mono.just(fromDb));

        StepVerifier.create(useCase.getProductCacheAside("1"))
                .expectNext(fromDb)
                .verifyComplete();

        verify(cacheGateway).getFromCache("1");
        verify(cacheGateway).saveInCache(any());
    }

    @Test
    void cacheAside_shouldThrowError_whenProductNotFound() {
        when(cacheGateway.getFromCache("99")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getProductCacheAside("99"))
                .expectErrorMatches(e -> e.getMessage().contains("Producto no encontrado con id: 99"))
                .verify();
    }

    @Test
    void writeThrough_shouldSaveInDbAndCacheInParallel() {
        Product product = Product.builder().id("10").name("Monitor").price(349.99).build();
        when(cacheGateway.saveInCache(product)).thenReturn(Mono.just(product));

        StepVerifier.create(useCase.saveProductWriteThrough(product))
                .expectNext(product)
                .verifyComplete();

        verify(cacheGateway).saveInCache(product);
    }

    @Test
    void writeBehind_shouldSaveInCacheAndReturnImmediately() {
        Product product = Product.builder().id("10").name("Monitor").price(349.99).build();
        when(cacheGateway.saveInCache(product)).thenReturn(Mono.just(product));

        StepVerifier.create(useCase.saveProductWriteBehind(product))
                .expectNext(product)
                .verifyComplete();

        verify(cacheGateway).saveInCache(product);
    }
}
