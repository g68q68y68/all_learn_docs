package com.example.search.doc;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ElasticSearchDocQuery {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public List<TaskDoc> queryTasksByUserId(String userId) {
        List<TaskDoc> ret = new ArrayList<>();
        try {
            QueryBuilder builder = QueryBuilders.termQuery("assignee", userId);
            Query query = new NativeSearchQuery(builder);
            query.addSort(Sort.by(Sort.Direction.DESC, "createdTime"));
            SearchHits<TaskDoc> searchHits = elasticsearchRestTemplate.search(query,
                    TaskDoc.class);
            for (SearchHit<TaskDoc> searchHit : searchHits.getSearchHits()) {
                ret.add(searchHit.getContent());
            }
        } catch (Exception ex) {
            log.error("Exception ex", ex);
        }
        for (TaskDoc taskDoc : ret) {
            log.info("任务ID：{},流程引擎：{}", taskDoc.getId(), taskDoc.getEngine());
        }
        return ret;
    }
}
