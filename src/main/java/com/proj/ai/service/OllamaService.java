package com.proj.ai.service;

import com.proj.ai.dto.OllamaChatRequest;
import com.proj.ai.dto.OllamaChatWithImageRequest;
import com.proj.ai.dto.OllamaResponse;

/**
 * Ollama 服务接口
 */
public interface OllamaService {
    
    /**
     * 文本对话
     */
    OllamaResponse chat(OllamaChatRequest request);
    
    /**
     * 图片+文本对话
     */
    OllamaResponse chatWithImage(OllamaChatWithImageRequest request);
}

