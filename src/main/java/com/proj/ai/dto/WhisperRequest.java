package com.proj.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * Whisper 语音识别请求
 */
@Data
@Schema(description = "Whisper语音识别请求")
public class WhisperRequest implements Serializable {
    
    @NotBlank(message = "音频文件Base64编码不能为空")
    @Schema(description = "音频文件Base64编码（支持 mp3、wav、m4a 等格式）", 
            example = "UklGRiQAAABXQVZFZm10...", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String audioBase64;
    
    @Schema(description = "语言代码（zh=中文, en=英文, auto=自动检测）", example = "zh")
    private String language = "zh";
    
    @Schema(description = "响应格式：json/text/verbose_json", example = "json")
    private String responseFormat = "json";
    
    @Schema(description = "是否翻译成英文", example = "false")
    private Boolean translate = false;
}

