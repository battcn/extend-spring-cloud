package com.battcn.cloud.extend.configuration.hystrix.propagate;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.battcn.cloud.extend.configuration.ExtendCloudBeanTemplate.HYSTRIX_PROPAGATE_REQUEST_ATTRIBUTE;

/**
 * @author Levin
 */
@Data
@ConfigurationProperties(HYSTRIX_PROPAGATE_REQUEST_ATTRIBUTE)
public class HystrixRequestAttributeProperties {

    /**
     * 启用hystrix传播HTTP请求和响应。默认为false。
     */
    private boolean enabled = false;
}