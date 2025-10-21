package com.proj.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Whisper 服务配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "whisper.server")
public class WhisperConfig {
    
    /**
     * Whisper 服务地址
     */
    private String url = "http://localhost:8083";
    
    /**
     * 推理接口路径
     */
    private String inferencePath = "/inference";
    
    /**
     * 请求超时时间（毫秒）
     */
    private Long timeout = 60000L;
    
    /**
     * 获取完整的推理接口 URL
     */
    public String getInferenceUrl() {
        return url + inferencePath;
    }
}

