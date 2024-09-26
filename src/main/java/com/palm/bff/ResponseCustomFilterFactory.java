package com.palm.bff;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ResponseCustomFilterFactory extends AbstractGatewayFilterFactory<ResponseCustomFilterFactory.Config> {

    @Getter
    @Setter
    public static class Config {
        private String param1;

    }

    private final ModifyResponseBodyGatewayFilterFactory modifyResponseBodyFilterFactory;
    private final ObjectMapper objectMapper;

    public ResponseCustomFilterFactory(ModifyResponseBodyGatewayFilterFactory modifyResponseBodyFilterFactory, ObjectMapper objectMapper) {
        super(Config.class);
        this.modifyResponseBodyFilterFactory = modifyResponseBodyFilterFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Config config) {
        ModifyResponseBodyGatewayFilterFactory.Config modifyResponseBodyFilterFactoryConfig = new ModifyResponseBodyGatewayFilterFactory.Config();

        modifyResponseBodyFilterFactoryConfig.setNewContentType(MediaType.APPLICATION_JSON_VALUE);
        modifyResponseBodyFilterFactoryConfig.setRewriteFunction(Map.class, Map.class, (exchange, bodyAsMap) -> {
            try {
                Map<String, Object> responseBody = (Map<String, Object>) bodyAsMap;
                log.info("Response body: {}", responseBody);


                return Mono.just(responseBody);
            } catch (Exception e) {
                e.printStackTrace();
                return Mono.error(e);
            }
        });

        return modifyResponseBodyFilterFactory.apply(modifyResponseBodyFilterFactoryConfig);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("param1");
    }
}

