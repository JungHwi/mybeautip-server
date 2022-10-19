package com.jocoos.mybeautip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.ReactiveCommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalancerDefaultMappingsProviderAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration;
import org.springframework.cloud.commons.config.CommonsConfigAutoConfiguration;
import org.springframework.cloud.commons.httpclient.HttpClientConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {ConfigurationPropertiesAutoConfiguration.class, ReactiveCommonsClientAutoConfiguration.class, SimpleDiscoveryClientAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, LifecycleAutoConfiguration.class,
        CommonsClientAutoConfiguration.class, HttpClientConfiguration.class, CommonsConfigAutoConfiguration.class, LoadBalancerDefaultMappingsProviderAutoConfiguration.class, CompositeDiscoveryClientAutoConfiguration.class,
        ServiceRegistryAutoConfiguration.class, LifecycleMvcEndpointAutoConfiguration.class, ApplicationAvailabilityAutoConfiguration.class, ProjectInfoAutoConfiguration.class})
@EnableScheduling
@EnableAsync
@EnableCaching
@EnableFeignClients
public class MyBeautipServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyBeautipServerApplication.class, args);
    }
}
