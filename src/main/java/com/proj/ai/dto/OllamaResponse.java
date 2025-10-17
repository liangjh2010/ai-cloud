package com.proj.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ollama 响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ollama 响应")
public class OllamaResponse {
    
    @Schema(description = "AI 响应内容")
    private String response;
    
    @Schema(description = "使用的模型名称")
    private String modelName;
    
    @Schema(description = "响应时间（毫秒）")
    private Long responseTime;
}

