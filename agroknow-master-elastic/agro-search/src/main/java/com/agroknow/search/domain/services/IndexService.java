package com.agroknow.search.domain.services;

import com.agroknow.search.domain.entities.UserQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author aggelos
 */
@Component
public class IndexService {

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    public IndexService() {}

    public void indexUserQuery(UserQuery userQuery) throws IOException {
        IndexResponse res = esClient.prepareIndex("history", "query")
            .setSource(objectMapper.writeValueAsString(userQuery))
            .execute()
            .actionGet();
    }
}
