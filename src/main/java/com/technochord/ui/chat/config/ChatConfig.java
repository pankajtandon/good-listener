package com.technochord.ui.chat.config;

import com.technochord.ui.chat.service.QueryService;
import org.apache.tomcat.util.http.parser.MediaTypeCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

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
                .requestFactory(clientHttpRequestFactory())
                .defaultHeader("User-Agent", "MyApp/1.0")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(180000);
        return factory;
    }
}
