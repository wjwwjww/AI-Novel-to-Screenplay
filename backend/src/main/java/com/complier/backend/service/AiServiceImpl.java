package com.complier.backend.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${ai.api.url}")
    private String defaultApiUrl;

    @Value("${ai.api.key}")
    private String defaultApiKey;

    @Value("${ai.model}")
    private String defaultModel;

    public AiServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    @Override
    public String generateScreenplay(String systemPrompt, String userPrompt, 
                                      String customApiUrl, String customApiKey, String customModel) {
        String apiUrl = (customApiUrl != null && !customApiUrl.isBlank()) ? customApiUrl : defaultApiUrl;
        String apiKey = (customApiKey != null && !customApiKey.isBlank()) ? customApiKey : defaultApiKey;
        String model = getActiveModel(customModel);

        if (apiKey == null || apiKey.isBlank() || "your-api-key-here".equals(apiKey)) {
            throw new IllegalArgumentException("API Key is missing. Please configure it in application.properties or provide a custom key in the request.");
        }

        String targetUrl = normalizeUrl(apiUrl);
        log.info("Calling LLM API: {} with model: {}", targetUrl, model);

        try {
            // Build OpenAI compatible request payload
            Map<String, Object> requestPayload = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)
                    ),
                    "temperature", 0.7
            );

            String requestBodyJson = objectMapper.writeValueAsString(requestPayload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .timeout(Duration.ofSeconds(120)) // Allow up to 2 mins for screenplay generation
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("LLM API request failed. Status: {}, Body: {}", response.statusCode(), response.body());
                throw new RuntimeException("AI API request failed with status code " + response.statusCode() + ": " + response.body());
            }

            JsonNode responseJson = objectMapper.readTree(response.body());
            JsonNode choices = responseJson.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                String content = choices.get(0).path("message").path("content").asText();
                if (content != null && !content.isBlank()) {
                    return content;
                }
            }

            throw new RuntimeException("Failed to extract content from AI response. Full response: " + response.body());
        } catch (Exception e) {
            log.error("Error calling AI API: ", e);
            throw new RuntimeException("Error communicating with AI service: " + e.getMessage(), e);
        }
    }

    @Override
    public String getActiveModel(String customModel) {
        return (customModel != null && !customModel.isBlank()) ? customModel : defaultModel;
    }

    /**
     * Clean and normalize API URLs to ensure the correct pathing for OpenAI-compatible endpoint.
     */
    private String normalizeUrl(String url) {
        url = url.trim();
        if (url.endsWith("/v1/chat/completions") || url.endsWith("/chat/completions")) {
            return url;
        }

        // Handle Gemini OpenAI endpoint specifically (e.g., ends in /openai or /openai/)
        if (url.contains("/openai")) {
            if (url.endsWith("/")) {
                return url + "chat/completions";
            } else {
                return url + "/chat/completions";
            }
        }
        
        if (url.endsWith("/v1") || url.endsWith("/v1/")) {
            if (url.endsWith("/")) {
                return url + "chat/completions";
            } else {
                return url + "/chat/completions";
            }
        }

        if (url.endsWith("/")) {
            return url + "v1/chat/completions";
        } else {
            return url + "/v1/chat/completions";
        }
    }
}
