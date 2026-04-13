package com.hexarch.api;

import com.hexarch.api.person.UserHandler;
import com.hexarch.api.product.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public RouterFunction<ServerResponse> hiRoutes(UserHandler userHandler) {
        return route()
                .POST("/api/v1/user", userHandler::create)
                .POST("/api/v1/user/login", userHandler::login)
                .GET("/api/v1/user", userHandler::securedPath)
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productRoutes(ProductHandler productHandler) {
        return route()
                .GET("/api/v1/product/cache-aside/{id}", productHandler::cacheAside)
                .POST("/api/v1/product/write-through", productHandler::writeThrough)
                .POST("/api/v1/product/write-behind", productHandler::writeBehind)
                .build();
    }
}
