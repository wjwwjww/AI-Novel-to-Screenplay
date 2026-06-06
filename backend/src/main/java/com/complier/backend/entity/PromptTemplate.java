package com.complier.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplate {
    private String id;
    private String name;
    private String description;
    private String systemPrompt;
}
