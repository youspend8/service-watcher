package io.assist.servicewatcher.config.properties;

import io.assist.servicewatcher.config.ApplicationProvider;
import io.assist.servicewatcher.vo.ProduceMode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import static org.springframework.util.StringUtils.hasText;

@Getter
@ToString
@ConfigurationProperties("service.watcher")
@ConstructorBinding
@RequiredArgsConstructor
public class ServiceWatcherProperties {
    private final String serviceName;
    private final ProduceMode produceMode;

    public String getServiceName() {
        return hasText(serviceName) ? serviceName : ApplicationProvider.getContext().getApplicationName();
    }
}
