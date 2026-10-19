package me.havooc.poligon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.havooc.poligon.model.SmsRequest;
import me.havooc.poligon.model.SmsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class SmsService {

    @Value("${smsapi.url}")
    private String apiUrl;

    @Value("${smsapi.token}")
    private String apiToken;

    @Value("${smsapi.from}")
    private String apiFrom;

    @Value("${smsapi.test}")
    private boolean apiTest;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SmsService(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public SmsResponse sendSms(SmsRequest request) throws IOException, InterruptedException {
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "to", request.phoneNumbers(),
                "message", request.message(),
                "from", apiFrom,
                "test", apiTest ? "1" : "0",
                "format", "json"
        ));

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(java.net.URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), SmsResponse.class);
    }
}
