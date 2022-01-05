package com.sample.movie.service;

import co.elastic.clients.elasticsearch._types.analysis.*;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.indices.*;
import com.sample.movie.repository.ElasticSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieIndexService {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void configIndex(String newIndexName) throws IOException {
        IndexSettingsAnalysis indexSettingsAnalysis = this.getMovieIndexSettingsAnalysis();
        TypeMapping mapping = this.getMovieTypeMapping();
        CreateIndexRequest request = new CreateIndexRequest.Builder()
                .index(newIndexName)
                .settings(new IndexSettings.Builder().analysis(indexSettingsAnalysis).build())
                .mappings(mapping)
                .build();
        CreateIndexResponse response = this.elasticSearchRepository.indexCreate(request);
        logger.info("configIndex finished : " + response);
    }

    public List<String> getIndexNameListOnAlias(String alias) throws IOException {
        List<String> indexListOnAlias = new ArrayList<>();
        GetAliasResponse response = this.elasticSearchRepository.getAllAlias();
        if (response.result().size() != 0) {
            response.result().forEach((index, metadata) -> {
                for (String _alias : metadata.aliases().keySet()) {
                    if (_alias.equals(alias)) {
                        indexListOnAlias.add(index);
                    }
                }
            });
        }
        return indexListOnAlias;
    }

    public void addAliasOnIndex(String index, String alias) throws IOException {
        this.elasticSearchRepository.addAlias(index, alias);
    }

    public void deleteIndex(List<String> indexList) {
        indexList.forEach((indexName) -> {
            try {
                this.elasticSearchRepository.deleteIndex(indexName);
            } catch (IOException e) {
                logger.error("failed to delete index : " + indexName, e);
            }
        });
    }

    public <T> void bulkIndex(String indexName, List<T> items) {
        List<BulkOperation> list = new ArrayList();
        items.forEach((item) -> {
            list.add(new BulkOperation.Builder()
                    .index(new IndexOperation.Builder<T>()
                            .index(indexName)
                            .document(item)
                            .build())
                    .build());
        });
        try {
            BulkRequest bulkRequest = new BulkRequest.Builder()
                    .operations(list)
                    .build();
            this.elasticSearchRepository.bulkIndex(bulkRequest);
        } catch (IOException e) {
            logger.error("bulk indexing error", e);
        }
    }

    private IndexSettingsAnalysis getMovieIndexSettingsAnalysis() {
        return new IndexSettingsAnalysis.Builder()
                .tokenizer("korean_analyzer", new Tokenizer.Builder().definition(
                        new TokenizerDefinition.Builder().noriTokenizer(
                                new NoriTokenizer.Builder().build()).build()).build())
                .tokenizer("ngram_tokenizer", new Tokenizer.Builder().definition(
                        new TokenizerDefinition.Builder().ngram(
                                new NGramTokenizer.Builder()
                                        .minGram(2)
                                        .maxGram(3)
                                        .tokenChars(TokenChar.Digit, TokenChar.Letter)
                                        .build()).build()).build())
                .tokenizer("edge_ngram_tokenizer", new Tokenizer.Builder().definition(
                        new TokenizerDefinition.Builder().edgeNgram(
                                new EdgeNGramTokenizer.Builder()
                                        .minGram(1)
                                        .maxGram(10)
                                        .tokenChars(TokenChar.Digit, TokenChar.Letter)
                                        .build()).build()).build())
                .analyzer("korean_analyzer", new Analyzer.Builder().custom(
                        new CustomAnalyzer.Builder()
                                .tokenizer("korean_analyzer")
                                .build()).build())
                .analyzer("ngram_analyzer", new Analyzer.Builder().custom(
                        new CustomAnalyzer.Builder()
                                .tokenizer("ngram_tokenizer")
                                .filter("lowercase", "trim")
                                .build()).build())
                .analyzer("edge_ngram_analyzer", new Analyzer.Builder().custom(
                        new CustomAnalyzer.Builder()
                                .tokenizer("edge_ngram_tokenizer")
                                .build()).build())
                .build();
    }

    private TypeMapping getMovieTypeMapping() {
        return new TypeMapping.Builder()
                .properties("movieCode", new Property.Builder().keyword(new KeywordProperty.Builder().build()).build())
                .properties("movieName", new Property.Builder()
                        .text(new TextProperty.Builder()
                                .analyzer("korean_analyzer")
                                .fields("keyword", new Property.Builder()
                                        .keyword(new KeywordProperty.Builder().ignoreAbove(256).build())
                                        .build())
                                .fields("ngram", new Property.Builder()
                                        .text(new TextProperty.Builder().analyzer("ngram_analyzer").build())
                                        .build())
                                .build())
                        .build())
                .properties("movieNameEnglish", new Property.Builder()
                        .text(new TextProperty.Builder()
                                .analyzer("standard")
                                .fields("keyword", new Property.Builder()
                                        .keyword(new KeywordProperty.Builder().ignoreAbove(256).build())
                                        .build())
                                .fields("ngram", new Property.Builder()
                                        .text(new TextProperty.Builder().analyzer("ngram_analyzer").build())
                                        .build())
                                .build())
                        .build())
                .properties("productYear", new Property.Builder()
                        .long_(new LongNumberProperty.Builder().nullValue(0L).build())
                        .build())
                .properties("openDate", new Property.Builder()
                        .long_(new LongNumberProperty.Builder().nullValue(0L).build())
                        .build())
                .properties("typeName", new Property.Builder()
                        .keyword(new KeywordProperty.Builder().build())
                        .build())
                .properties("productStateName", new Property.Builder()
                        .keyword(new KeywordProperty.Builder().build())
                        .build())
                .properties("nations", new Property.Builder()
                        .keyword(new KeywordProperty.Builder().build())
                        .build())
                .properties("genres", new Property.Builder()
                        .keyword(new KeywordProperty.Builder().build())
                        .build())
                .properties("representativeNationName", new Property.Builder()
                        .text(new TextProperty.Builder()
                                .analyzer("ngram_analyzer")
                                .fields("keyword", new Property.Builder()
                                        .keyword(new KeywordProperty.Builder().ignoreAbove(256).build()).build())
                                .build())
                        .build())
                .properties("representativeGenreName", new Property.Builder()
                        .text(new TextProperty.Builder()
                                .analyzer("ngram_analyzer")
                                .fields("keyword", new Property.Builder()
                                        .keyword(new KeywordProperty.Builder().ignoreAbove(256).build()).build())
                                .build())
                        .build())
                .properties("viewCount", new Property.Builder()
                        .long_(new LongNumberProperty.Builder().nullValue(0L).build())
                        .build())
                .build();

    }

}
