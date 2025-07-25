package com.technochord.ui.chat.service;

import com.technochord.ui.chat.support.QueryRequest;
import com.technochord.ui.chat.support.QueryResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.client.RestClient;


@Log4j2
public class QueryService {
    private RestClient restClient;

    public QueryService(final RestClient restClient) {
        this.restClient = restClient;
    }

    public String process(final String query) {
        QueryRequest queryRequest = new QueryRequest(query, 4);
        log.debug("Going to query with " + queryRequest);
        QueryResponse queryResponse =  restClient.post()
                .uri("/planner/query")
                .body(queryRequest)
                .retrieve()
                .body(QueryResponse.class);
        log.debug("Response is {}", queryResponse);
        return queryResponse.getAnswer();
    }
}
