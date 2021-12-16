package io.assist.servicewatcher.config.properties;

import io.assist.servicewatcher.config.properties.es.ForwardProxySourceProperties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

import static java.util.Objects.nonNull;

@Getter
@ToString
@ConfigurationProperties("service.watcher.es")
@ConditionalOnProperty(prefix = "service.watcher", name = "es")
@ConstructorBinding
@RequiredArgsConstructor
@EnableConfigurationProperties({ ForwardProxySourceProperties.class })
public class ServiceWatcherElasticsearchProperties {
    private final String host;
    private final String username;
    private final String password;
    private final int port;
    private final List<ForwardProxySourceProperties> forwardProxy;

    public boolean existsForwardProxy() {
        return nonNull(forwardProxy) && !forwardProxy.isEmpty();
    }
}
