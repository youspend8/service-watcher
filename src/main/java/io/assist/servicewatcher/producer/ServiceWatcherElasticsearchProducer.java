package io.assist.servicewatcher.producer;

import io.assist.servicewatcher.config.client.es.ServiceWatcherElasticsearchClient;
import io.assist.servicewatcher.vo.ServiceWatcherEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(ServiceWatcherElasticsearchClient.class)
public class ServiceWatcherElasticsearchProducer implements ServiceWatcherEventProducer {
    private final ServiceWatcherElasticsearchClient serviceWatcherElasticsearchClient;

    @PostConstruct
    private void postConstruct() {
        log.info("ServiceWatcher >> Created ServiceWatcher Elasticsearch Producer");
    }

    @Override
    public void produce(ServiceWatcherEvent event) {
        serviceWatcherElasticsearchClient.save(event);
    }
}
