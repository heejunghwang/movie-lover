package com.sample.movie.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.elasticsearch.indices.update_aliases.Action;
import co.elastic.clients.elasticsearch.indices.update_aliases.AddAction;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.ConnectException;

@Repository
public class ElasticSearchRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public HealthResponse getHealth() throws IOException {
        try {
            return elasticsearchClient.cluster().health();
        } catch (ConnectException e) {
            logger.error("elasticsearch connection error", e);
            throw e;
        }
    }

    public <TDocument> SearchResponse<TDocument> search(SearchRequest request, Class<TDocument> tDocumentClass) throws IOException {
        Gson gson = new Gson();
        String jsonString = gson.toJson(request);
        logger.info("elasticsearch request ::::: " + jsonString);
        return elasticsearchClient.search(request, tDocumentClass);
    }

    public CreateIndexResponse indexCreate(CreateIndexRequest request) throws IOException {
        return elasticsearchClient.indices().create(request);
    }

    public DeleteIndexResponse deleteIndex(String oldIndexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest.Builder().index(oldIndexName).build();
        return elasticsearchClient.indices().delete(request);
    }

    public GetAliasResponse getAllAlias() throws IOException {
        return elasticsearchClient.indices().getAlias(new GetAliasRequest.Builder().build());
    }

    public UpdateAliasesResponse addAlias(String indexName, String aliasName) throws IOException {
        UpdateAliasesRequest request = new UpdateAliasesRequest.Builder()
                .actions(new Action.Builder()
                        .add(new AddAction.Builder()
                                .index(indexName)
                                .alias(aliasName)
                                .build())
                        .build())
                .build();
        return elasticsearchClient.indices().updateAliases(request);
    }

    public BulkResponse bulkIndex(BulkRequest bulkRequest) throws IOException {
        Gson gson = new Gson();
        String jsonString = gson.toJson(bulkRequest);
        logger.info("elasticsearch request ::::: " + jsonString);
        return elasticsearchClient.bulk(bulkRequest);
    }

}
