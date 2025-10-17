package com.proj.ai.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

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
    
    @Value("${spring.ai.ollama.base-url:http://35.221.238.240:11434}")
    private String ollamaBaseUrl;
    
    private WebClient webClient;
    
    @PostConstruct
    public void init() {
        log.info("初始化 OllamaClient，baseUrl: {}", ollamaBaseUrl);
        this.webClient = WebClient.builder()
                .baseUrl(ollamaBaseUrl)
                .build();
        log.info("WebClient 初始化完成");
    }
    
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
     * 使用原生 HTTP API 调用 Ollama 的多模态功能
     * 支持 imageBase64 和 imageUrl 两种输入方式，imageBase64 优先
     * 
     * @param prompt 提示词
     * @param imageBase64 图片 Base64 编码（优先使用）
     * @param imageUrl 图片 URL（当 imageBase64 为空时使用）
     * @return AI 响应内容
     */
    public String chatWithImage(String prompt, String imageBase64, String imageUrl) {
        try {
            log.info("发送图片+文本请求到 Ollama, prompt length: {}, imageBase64: {}, imageUrl: {}", 
                    prompt.length(), imageBase64 != null ? "有值" : "无值", imageUrl != null ? "有值" : "无值");
            long startTime = System.currentTimeMillis();
            
            String finalImageBase64;
            
            // 1. 优先使用 imageBase64，如果为空则从 imageUrl 下载
            if (imageBase64 != null && !imageBase64.trim().isEmpty()) {
                log.info("使用 imageBase64 输入");
                finalImageBase64 = cleanBase64Prefix(imageBase64);
            } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                log.info("使用 imageUrl 输入，开始下载图片: {}", imageUrl);
                finalImageBase64 = downloadImageAsBase64(imageUrl);
            } else {
                throw new IllegalArgumentException("图片不能为空，请提供 imageBase64 或 imageUrl");
            }
            
            log.debug("最终使用的 Base64 长度: {}", finalImageBase64.length());
            
            // 2. 构建包含图片的提示词（参考案例1的方式）
            String userPrompt = "<image>" + finalImageBase64 + "</image>\n" + prompt;
            
            // 3. 构建请求体
            Map<String, Object> body = Map.of(
                    "model", defaultModel,
                    "prompt", userPrompt,
                    "temperature", 0.1
            );
            
            // 4. 调用 Ollama 原生 API
            log.info("准备调用 Ollama API，URL: {}/api/generate", ollamaBaseUrl);
            String result = webClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // 同步调用，简化实现
            
            long endTime = System.currentTimeMillis();
            log.info("Ollama 图片识别完成, 耗时: {}ms, 响应长度: {}", 
                    (endTime - startTime), result != null ? result.length() : 0);
            
            return result != null ? result : "图片识别失败，未收到响应";
            
        } catch (IllegalArgumentException e) {
            log.error("参数错误: {}", e.getMessage());
            throw new RuntimeException("参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("Ollama 图片识别失败: {}", e.getMessage(), e);
            throw new RuntimeException("Ollama 图片识别失败: " + e.getMessage());
        }
    }
    
    /**
     * 从 URL 下载图片并转换为 Base64
     * 
     * @param imageUrl 图片 URL
     * @return Base64 编码的图片数据
     */
    private String downloadImageAsBase64(String imageUrl) {
        try {
            log.info("开始下载图片: {}", imageUrl);
            
            // 使用 WebClient 下载图片
            byte[] imageBytes = webClient.get()
                    .uri(imageUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            
            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("下载的图片为空");
            }
            
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            log.info("图片下载成功，大小: {} bytes, Base64 长度: {}", imageBytes.length, base64.length());
            
            return base64;
            
        } catch (Exception e) {
            log.error("下载图片失败: {}", e.getMessage(), e);
            throw new RuntimeException("下载图片失败: " + e.getMessage());
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

