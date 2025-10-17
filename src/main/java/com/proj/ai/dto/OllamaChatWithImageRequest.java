package com.proj.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Ollama 图片+文本对话请求
 */
@Data
@Schema(description = "Ollama 图片+文本对话请求")
public class OllamaChatWithImageRequest {
    
    @Schema(description = "提示词", required = true, example = "请描述这张图片中的场景")
    @NotBlank(message = "提示词不能为空")
    private String prompt;
    
    @Schema(description = "图片 Base64 编码", required = true)
    @NotBlank(message = "图片不能为空")
    private String image;
}

