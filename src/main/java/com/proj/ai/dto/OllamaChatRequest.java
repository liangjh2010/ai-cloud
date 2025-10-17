package com.proj.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Ollama 文本对话请求
 */
@Data
@Schema(description = "Ollama 文本对话请求")
public class OllamaChatRequest {
    
    @Schema(description = "提示词", required = true, example = "你好，请介绍一下你自己")
    @NotBlank(message = "提示词不能为空")
    private String prompt;
}

