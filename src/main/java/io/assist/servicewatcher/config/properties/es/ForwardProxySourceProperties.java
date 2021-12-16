package io.assist.servicewatcher.config.properties.es;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ToString
@ConfigurationProperties("service.watcher.es.forward-proxy")
@ConditionalOnProperty(prefix = "service.watcher.es.forward-proxy", name = "source")
@ConstructorBinding
@RequiredArgsConstructor
public class ForwardProxySourceProperties {
    private final String host;
    private final int port;
    private final String destination;
}
