package com.proj.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Whisper 语音识别响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Whisper语音识别响应")
public class WhisperResponse implements Serializable {
    
    @Schema(description = "识别的文本内容", example = "今天天气真好")
    private String text;
    
    @Schema(description = "识别的语言", example = "zh")
    private String language;
    
    @Schema(description = "处理时间（毫秒）", example = "2500")
    private Long processingTime;
    
    @Schema(description = "是否成功", example = "true")
    private Boolean success;
    
    @Schema(description = "错误信息（如果失败）", example = "")
    private String errorMessage;
}

