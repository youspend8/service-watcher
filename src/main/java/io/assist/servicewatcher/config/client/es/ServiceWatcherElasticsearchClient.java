package io.assist.servicewatcher.config.client.es;

import io.assist.servicewatcher.config.client.es.template.ElasticsearchTemplateFactory;
import io.assist.servicewatcher.config.properties.ServiceWatcherProperties;
import io.assist.servicewatcher.vo.ApiEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.ComposableIndexTemplateExistRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static io.assist.servicewatcher.util.JsonUtil.asJson;

@Slf4j
@RequiredArgsConstructor
@Component
public class ServiceWatcherElasticsearchClient {
    private final ServiceWatcherProperties serviceWatcherProperties;
    private final RestHighLevelClient restHighLevelClient;
    private static final String DEFAULT_INDEX_PREFIX = "api";
    private static final String DEFAULT_INDEX_TEMPLATE = "api";

    @PostConstruct
    private void postConstruct() {
        try {
            log.info("Created [{}] ServiceWatcher Elasticsearch Client", serviceWatcherProperties.getServiceName());
            boolean existsDefaultIndexTemplate = restHighLevelClient
                    .indices()
                    .existsIndexTemplate(new ComposableIndexTemplateExistRequest(DEFAULT_INDEX_TEMPLATE), RequestOptions.DEFAULT);
            if (!existsDefaultIndexTemplate) {
                createIndexTemplate();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(Object document) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(String.format("%s-%s", DEFAULT_INDEX_PREFIX, serviceWatcherProperties.getServiceName()));
        updateRequest.doc(asJson(document), XContentType.JSON);
        updateRequest.id(UUID.randomUUID().toString());
        updateRequest.docAsUpsert(true);
        restHighLevelClient.updateAsync(updateRequest, RequestOptions.DEFAULT, new ActionListener<>() {
            @Override
            public void onResponse(UpdateResponse updateResponse) {
                log.debug("elasticsearch document async update success");
            }

            @Override
            public void onFailure(Exception e) {
                log.error("elasticsearch document update failed");
                e.printStackTrace();
            }
        });
    }

    private void createIndexTemplate() {
        try {
            PutIndexTemplateRequest request = new PutIndexTemplateRequest(DEFAULT_INDEX_TEMPLATE);
            request.patterns(Collections.singletonList(DEFAULT_INDEX_TEMPLATE + "*"));
            request.settings(Settings.builder()
                    .put("refresh_interval", "5s")
                    .put("number_of_shards", 1)
                    .put("number_of_replicas", 1)
                    .build());
            request.mapping(ElasticsearchTemplateFactory.createWithReflection(ApiEvent.class));
            restHighLevelClient.indices().putTemplate(request, RequestOptions.DEFAULT);
            log.info("created elasticsearch default index template");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed elasticsearch default index template");
        }
    }
}
