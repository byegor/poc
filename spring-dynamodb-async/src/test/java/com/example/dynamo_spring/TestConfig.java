package com.example.dynamo_spring;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {
    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        return new NettyReactiveWebServerFactory();
    }
}
