package com.complier.backend.service;

public interface AiService {
    /**
     * Call the LLM API to generate a screenplay from the given prompt.
     *
     * @param systemPrompt Instructions defining the screenplay style/format.
     * @param userPrompt The novel text.
     * @param customApiUrl Optional custom API base URL to override defaults.
     * @param customApiKey Optional custom API key to override defaults.
     * @param customModel Optional custom model name to override defaults.
     * @return Generated screenplay text.
     */
    String generateScreenplay(String systemPrompt, String userPrompt, 
                              String customApiUrl, String customApiKey, String customModel);

    /**
     * Get the model name that will be used (either custom or default).
     */
    String getActiveModel(String customModel);
}
