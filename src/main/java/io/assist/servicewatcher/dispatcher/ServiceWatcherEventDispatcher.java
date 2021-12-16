package io.assist.servicewatcher.dispatcher;

import io.assist.servicewatcher.producer.ServiceWatcherEventProducer;
import io.assist.servicewatcher.vo.ServiceWatcherEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ServiceWatcherEventDispatcher {
    private final List<ServiceWatcherEventProducer> producers;

    @PostConstruct
    private void postConstruct() {
        if (producers.size() == 0) {
            log.error("If @EnableServiceWatcher is enabled, at least one producer is configured");
        } else {
            log.info("ServiceWatcher Event Dispatcher created, active producers --> {}", producers);
        }
    }

    public void dispatch(ServiceWatcherEvent event) {
        if (producers.size() != 0) {
            log.info("ServiceWatcher Event Dispatching --> {}", event);
            producers.forEach(producer -> producer.produce(event));
        }
    }
}
