package com.market.market.common;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@RequiredArgsConstructor
public class MappingDebugConfig {

    private final RequestMappingHandlerMapping handlerMapping;

    @PostConstruct
    public void printMappings() {
        System.out.println("==== ALL REQUEST MAPPINGS ====");
        handlerMapping.getHandlerMethods().forEach((info, method) -> {
            System.out.println(info + " => " + method);
        });
        System.out.println("==== END REQUEST MAPPINGS ====");
    }
}