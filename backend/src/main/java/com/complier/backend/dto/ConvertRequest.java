package com.complier.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvertRequest {
    private String title;
    private String novelText;
    private String promptStyle;
    
    // Optional parameters to override application.properties config on-the-fly
    private String apiKey;
    private String apiUrl;
    private String model;
}
