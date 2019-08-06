package com.battcn.cloud.extend.configuration.gateway.filter;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

import static com.battcn.cloud.extend.configuration.ExtendCloudBeanTemplate.GATEWAY_FILTER_BLACK_WHITE_LIST;

/**
 * @author Levin
 */
@Data
@ConfigurationProperties(GATEWAY_FILTER_BLACK_WHITE_LIST)
public class BlackWhiteListProperties {

    /**
     * 启用hystrix传播HTTP请求和响应。默认为false。
     */
    private boolean enabled = false;
    private String filterName = "BlackWhiteList";
    private Map<String,Object> result;
}