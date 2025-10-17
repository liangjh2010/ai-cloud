package com.proj.ai.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.Base64;
import java.util.List;

/**
 * Ollama 客户端
 * 使用 Spring AI 1.0.3 调用 Ollama 服务
 */
@Slf4j
@Component
public class OllamaClient {
    
    @Autowired
    private ChatModel chatModel;
    
    @Value("${spring.ai.ollama.chat.options.model:qwen2.5vl:3b}")
    private String defaultModel;
    
    /**
     * 发送文本请求到 Ollama
     * 
     * @param prompt 提示词
     * @return AI 响应内容
     */
    public String chat(String prompt) {
        try {
            log.info("发送文本请求到 Ollama, prompt length: {}", prompt.length());
            long startTime = System.currentTimeMillis();
            
            ChatResponse response = chatModel.call(new Prompt(prompt));
//            String result = response.getResult().getOutput().getContent();
            String result = response.getResult().getOutput().getText();
            
            long endTime = System.currentTimeMillis();
            log.info("Ollama 文本响应完成, 耗时: {}ms, 响应长度: {}", 
                    (endTime - startTime), result.length());
            
            return result;
            
        } catch (Exception e) {
            log.error("Ollama 文本对话失败: {}", e.getMessage(), e);
            throw new RuntimeException("Ollama 调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送图片+文本请求到 Ollama（用于场景识别）
     * 暂时简化实现，只返回文本提示
     * 
     * @param prompt 提示词
     * @param imageBase64 图片 Base64 编码
     * @return AI 响应内容
     */
    public String chatWithImage(String prompt, String imageBase64) {
        try {
            log.info("发送图片+文本请求到 Ollama, prompt length: {}, image length: {}", 
                    prompt.length(), imageBase64.length());
            
            // 暂时简化：只处理文本，忽略图片
            // TODO: 实现真正的多模态功能
            return chat(prompt + " [图片已接收但暂未处理]");
            
        } catch (Exception e) {
            log.error("Ollama 图片识别失败: {}", e.getMessage(), e);
            throw new RuntimeException("Ollama 图片识别失败: " + e.getMessage());
        }
    }
    
    /**
     * 清理 Base64 前缀
     */
    private String cleanBase64Prefix(String base64) {
        if (base64.contains(",")) {
            return base64.split(",")[1];
        }
        return base64;
    }
    
    /**
     * 获取当前使用的模型名称
     */
    public String getModelName() {
        return defaultModel;
    }
}

