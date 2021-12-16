package io.assist.servicewatcher.config.client.es;

import io.assist.servicewatcher.config.properties.ServiceWatcherElasticsearchProperties;
import io.assist.servicewatcher.config.properties.ServiceWatcherProperties;
import io.assist.servicewatcher.config.properties.es.ForwardProxySourceProperties;
import io.assist.servicewatcher.producer.ServiceWatcherElasticsearchProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "service.watcher", name = "produce-mode", havingValue = "es")
@Import({ ServiceWatcherElasticsearchClient.class, ServiceWatcherElasticsearchProducer.class })
public class ServiceWatcherElasticsearchConfiguration {
    public static final String BEAN_NAME = "restHighLevelClient";
    private final ServiceWatcherProperties serviceWatcherProperties;
    private final ServiceWatcherElasticsearchProperties serviceWatcherElasticsearchProperties;

    @Bean(name = BEAN_NAME)
    public RestHighLevelClient client() {
        validateProperties();
        log.info("Connecting [{}] ServiceWatcher Elasticsearch Client...", serviceWatcherProperties.getServiceName());
        try {
            RestClientBuilder restClientBuilder = RestClient.builder(getHost())
                    .setHttpClientConfigCallback(httpClientBuilder -> {
                        httpClientBuilder.setDefaultCredentialsProvider(getCredentialsProvider());
                        if (serviceWatcherElasticsearchProperties.existsForwardProxy()) {
                            httpClientBuilder.setProxy(getProxyHost());
                        }
                        return httpClientBuilder;
                    });
            return new RestHighLevelClient(restClientBuilder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private CredentialsProvider getCredentialsProvider() {
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        basicCredentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "firefox"));
        return basicCredentialsProvider;
    }

    private HttpHost getProxyHost() {
        List<ForwardProxySourceProperties> forwardProxies = serviceWatcherElasticsearchProperties.getForwardProxy();
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            ForwardProxySourceProperties properties = forwardProxies.stream()
                    .filter(forwardProxy -> forwardProxy.getHost().equals(host))
                    .findFirst()
                    .orElse(null);
            if (isNull(properties)) {
                log.error("Not matched current InetAddress.getLocalHost().getHostAddress() --> {} and forward proxy host:port", host);
                return null;
            }
            String[] hostAndPort = properties.getDestination().split(":");
            if (hostAndPort.length != 2) {
                throw new RuntimeException("service.watcher.es.forward-proxy.destination property's format is host:port");
            }
            return new HttpHost(hostAndPort[0], Integer.parseInt(hostAndPort[1]));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpHost getHost() {
        return new HttpHost(
                serviceWatcherElasticsearchProperties.getHost(),
                serviceWatcherElasticsearchProperties.getPort()
        );
    }

    private void validateProperties() {
        if (isNull(serviceWatcherElasticsearchProperties.getHost())) {
            throw new RuntimeException("Property 'service.watcher.es.host' must be configured");
        }
        if (isNull(serviceWatcherElasticsearchProperties.getPassword())) {
            throw new RuntimeException("Property 'service.watcher.es.password' must be configured");
        }
        if (isNull(serviceWatcherElasticsearchProperties.getUsername())) {
            throw new RuntimeException("Property 'service.watcher.es.username' must be configured");
        }
    }
}
