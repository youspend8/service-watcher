package io.assist.servicewatcher.autoconfigure;

import io.assist.servicewatcher.aspect.ServiceApiWatchAspect;
import io.assist.servicewatcher.aspect.ServiceApiWatchFilter;
import io.assist.servicewatcher.config.client.es.ServiceWatcherElasticsearchConfiguration;
import io.assist.servicewatcher.dispatcher.ServiceWatcherEventDispatcher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = "io.assist.servicewatcher.bean")
@Import({
        ServiceApiWatchAspect.class,
        ServiceApiWatchFilter.class,
        ServiceWatcherElasticsearchConfiguration.class,
        ServiceWatcherEventDispatcher.class,
})
public class ServiceWatcherAutoConfiguration {
}
