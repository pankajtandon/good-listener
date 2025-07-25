package com.technochord.ui.chat.config;

import com.technochord.ui.chat.service.QueryService;
import org.apache.tomcat.util.http.parser.MediaTypeCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class ChatConfig {

    @Value("${backend-host}")
    private String backendHost;

    @Bean
    public QueryService queryService() {
        return new QueryService(restClient());
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(backendHost)
                .defaultHeader("User-Agent", "MyApp/1.0")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
