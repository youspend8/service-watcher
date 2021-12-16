package io.assist.servicewatcher.config;

import io.assist.servicewatcher.autoconfigure.ServiceWatcherAutoConfiguration;
import io.assist.servicewatcher.config.properties.ServiceWatcherElasticsearchProperties;
import io.assist.servicewatcher.config.properties.ServiceWatcherProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ ServiceWatcherAutoConfiguration.class })
@EnableConfigurationProperties({ ServiceWatcherProperties.class, ServiceWatcherElasticsearchProperties.class })
public @interface EnableServiceWatcher {
}
