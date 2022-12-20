package com.example.demo;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@Component
class Filter implements WebFilter {
    private final AtomicInteger cancelCounter = new AtomicInteger(0);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange).transform(this::filterInternal);
    }

    private Publisher<Void> filterInternal(Mono<Void> call) {
        return call.doOnCancel(() -> {
            var total = cancelCounter.incrementAndGet();
            LOGGER.info("Call already canceled. Total canceled: " + total, new Exception());
        });
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Filter.class);
}