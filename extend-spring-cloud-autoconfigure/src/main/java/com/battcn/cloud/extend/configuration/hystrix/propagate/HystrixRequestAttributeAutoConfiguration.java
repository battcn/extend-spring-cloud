package com.battcn.cloud.extend.configuration.hystrix.propagate;


import com.netflix.hystrix.Hystrix;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.battcn.cloud.extend.configuration.ExtendCloudBeanTemplate.*;

/**
 * Hystrix 线程隔离请求
 *
 * @author Levin
 */
@Configuration
@ConditionalOnClass({Hystrix.class})
@ConditionalOnProperty(prefix = HYSTRIX_PROPAGATE_REQUEST_ATTRIBUTE, name = ENABLED, havingValue = TRUE, matchIfMissing = true)
@EnableConfigurationProperties(HystrixRequestAttributeProperties.class)
public class HystrixRequestAttributeAutoConfiguration {

    @Bean
    public RequestAttributeHystrixConcurrencyStrategy hystrixRequestAutoConfiguration() {
        return new RequestAttributeHystrixConcurrencyStrategy();
    }
}