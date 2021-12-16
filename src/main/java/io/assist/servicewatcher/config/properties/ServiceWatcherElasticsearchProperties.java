package io.assist.servicewatcher.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ToString
@ConfigurationProperties("service.watcher.es")
@ConditionalOnProperty(prefix = "service.watcher", name = "es")
@ConstructorBinding
@RequiredArgsConstructor
public class ServiceWatcherElasticsearchProperties {
    private final String host;
    private final String username;
    private final String password;
    private final int port;
}
