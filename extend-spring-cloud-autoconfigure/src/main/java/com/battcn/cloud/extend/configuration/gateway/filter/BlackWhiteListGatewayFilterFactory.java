package com.battcn.cloud.extend.configuration.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ipresolver.XForwardedRemoteAddressResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static com.battcn.cloud.extend.configuration.ExtendCloudBeanTemplate.*;

/**
 * XForwardedRemoteAddressResolver
 * 黑白名单过滤器
 *
 * @author Levin
 */
@Slf4j
@Order(99)
@Configuration
@EnableConfigurationProperties(BlackWhiteListProperties.class)
@ConditionalOnProperty(prefix = GATEWAY_FILTER_BLACK_WHITE_LIST, name = ENABLED, havingValue = TRUE, matchIfMissing = true)
public class BlackWhiteListGatewayFilterFactory extends AbstractGatewayFilterFactory<BlackWhiteListGatewayFilterFactory.Config> {

    private static final String DEFAULT_FILTER_NAME = "BlackWhiteList";

    private final BlackWhiteListProperties properties;

    @Override
    public String name() {
        return StringUtils.isEmpty(properties.getFilterName()) ? DEFAULT_FILTER_NAME : properties.getFilterName();
    }

    @Autowired
    public BlackWhiteListGatewayFilterFactory(BlackWhiteListProperties properties) {
        super(Config.class);
        this.properties = properties;
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            InetSocketAddress remoteAddress = XForwardedRemoteAddressResolver.maxTrustedIndex(1).resolve(exchange);
            String ip = remoteAddress.getAddress().getHostAddress();
            log.debug("[访问者IP地址] - [{}]", ip);
            if (config.type == BlackWhiteListType.BLACK_LIST) {
                boolean access = config.getWhiteLists().contains(ip);
                if (access) {
                    return chain.filter(exchange);
                }
            }
            if (config.type == BlackWhiteListType.WHITE_LIST) {
                boolean access = config.getWhiteLists().contains(ip);
                if (!access) {
                    return chain.filter(exchange);
                }
            }
            log.warn("[访问受限，该地址在黑名单或者不在白名单里] - [{}]", ip);
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] byteBuffer = new byte[0];
            try {
                Map<String, Object> result = properties.getResult();
                if (result == null) {
                    result = new Hashtable<>(4);
                    result.put("messageId", HttpStatus.FORBIDDEN.value());
                    result.put("message", "访问受限，请联系管理员");
                    result.put("successful", false);
                    result.put("timestamp", System.currentTimeMillis());
                }
                byteBuffer = objectMapper.writeValueAsBytes(result);
            } catch (JsonProcessingException e) {
                log.error("[JSON异常]", e);
            }
            return response.writeWith(Mono.just(response.bufferFactory().wrap(byteBuffer)));
        };
    }

    @Data
    public static class Config {

        private Integer maxTrustedIndex = 1;
        private BlackWhiteListType type;
        private List<String> blackLists;
        private List<String> whiteLists;
    }

    @AllArgsConstructor
    public enum BlackWhiteListType {
        /**
         * 黑名单
         */
        BLACK_LIST,
        /**
         * 白名单
         */
        WHITE_LIST;

    }

}
