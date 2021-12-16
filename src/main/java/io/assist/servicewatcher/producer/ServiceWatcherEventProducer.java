package io.assist.servicewatcher.producer;

import io.assist.servicewatcher.vo.ServiceWatcherEvent;

public interface ServiceWatcherEventProducer {
    void produce(ServiceWatcherEvent event);
}
