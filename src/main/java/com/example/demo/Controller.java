package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/v1/hello")
public class Controller {

    @GetMapping("/delay/mono")
    public Mono<String> delayMono() {
        return Mono.just("Hello").delayElement(Duration.ofMillis(1));
    }

    @GetMapping("/delay/flux")
    public Flux<String> delayFlux() {
        return Flux.just("Hello").delayElements(Duration.ofMillis(1));
    }
}
