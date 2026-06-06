package com.complier.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvertResponse {
    private String screenplayText;
    private Long historyId;
    private String modelUsed;
}
