package com.technochord.ui.chat.service;

import com.technochord.ui.chat.support.ConfirmationOrSkipRequest;
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

    public QueryResponse processInput(final String query, String topK, String modelName, String temperature) {
        QueryRequest queryRequest = new QueryRequest(query, topK, modelName, temperature);
        log.debug("Going to query with " + queryRequest);
        QueryResponse queryResponse =  restClient.post()
                .uri("/planner/api/ai/chat")
                .body(queryRequest)
                .retrieve()
                .body(QueryResponse.class);
        log.debug("Response of query is {}", queryResponse);
        return queryResponse;
    }

    public QueryResponse processConfirmOrSkip(String conversationId, boolean approved, String feedback, String modelName) {
        ConfirmationOrSkipRequest confirmationOrSkipRequest = new ConfirmationOrSkipRequest(conversationId, approved, feedback, modelName);
        log.debug("Going to confirm with " + confirmationOrSkipRequest);
        QueryResponse queryResponse =  restClient.post()
                .uri("/planner/api/ai/confirm-tool")
                .body(confirmationOrSkipRequest)
                .retrieve()
                .body(QueryResponse.class);
        log.debug("Response of confirm is {}", queryResponse);
        return queryResponse;
    }
}
