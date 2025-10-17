package com.proj.ai.service.impl;

import com.proj.ai.client.OllamaClient;
import com.proj.ai.dto.OllamaChatRequest;
import com.proj.ai.dto.OllamaChatWithImageRequest;
import com.proj.ai.dto.OllamaResponse;
import com.proj.ai.service.OllamaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Ollama 服务实现
 */
@Slf4j
@Service
public class OllamaServiceImpl implements OllamaService {
    
    @Autowired
    private OllamaClient ollamaClient;
    
    /**
     * 文本对话
     */
    @Override
    public OllamaResponse chat(OllamaChatRequest request) {
        log.info("处理文本对话请求");
        
        long startTime = System.currentTimeMillis();
        
        // 调用 Ollama
        String response = ollamaClient.chat(request.getPrompt());
        
        long endTime = System.currentTimeMillis();
        
        // 构建响应
        return OllamaResponse.builder()
                .response(response)
                .modelName(ollamaClient.getModelName())
                .responseTime(endTime - startTime)
                .build();
    }
    
    /**
     * 图片+文本对话
     */
    @Override
    public OllamaResponse chatWithImage(OllamaChatWithImageRequest request) {
        log.info("处理图片+文本对话请求");
        
        long startTime = System.currentTimeMillis();
        
        // 调用 Ollama（支持 imageBase64 和 imageUrl 两种方式）
        String response = ollamaClient.chatWithImage(
                request.getPrompt(), 
                request.getImage(),
                request.getImageUrl()
        );
        
        long endTime = System.currentTimeMillis();
        
        // 构建响应
        return OllamaResponse.builder()
                .response(response)
                .modelName(ollamaClient.getModelName())
                .responseTime(endTime - startTime)
                .build();
    }
}

