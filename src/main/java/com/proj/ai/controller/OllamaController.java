package com.proj.ai.controller;

// import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.proj.ai.common.Result;
import com.proj.ai.dto.OllamaChatRequest;
import com.proj.ai.dto.OllamaChatWithImageRequest;
import com.proj.ai.dto.OllamaResponse;
import com.proj.ai.service.OllamaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Ollama 接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ollama")
@Tag(name = "Ollama 接口", description = "Ollama 大模型调用接口")
@CrossOrigin(origins = "*")
public class OllamaController {
    
    @Autowired
    private OllamaService ollamaService;
    
    /**
     * 文本对话接口
     */
    @PostMapping("/chat")
    @Operation(summary = "文本对话", description = "发送纯文本请求到 Ollama")
    public Result<OllamaResponse> chat(@Valid @RequestBody OllamaChatRequest request) {
        try {
            log.info("收到文本对话请求");
            OllamaResponse response = ollamaService.chat(request);
            return Result.ok(response);
        } catch (Exception e) {
            log.error("文本对话失败: {}", e.getMessage(), e);
            return Result.error("文本对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 图片+文本对话接口
     */
    @PostMapping("/chat-with-image")
    @Operation(summary = "图片+文本对话", description = "发送图片+文本请求到 Ollama（用于场景识别）")
    public Result<OllamaResponse> chatWithImage(@Valid @RequestBody OllamaChatWithImageRequest request) {
        try {
            log.info("收到图片+文本对话请求");
            OllamaResponse response = ollamaService.chatWithImage(request);
            log.info("图片识别成功，准备返回结果: {}", response);
            return Result.ok(response);
        } catch (Exception e) {
            log.error("图片+文本对话失败: {}", e.getMessage(), e);
            return Result.error("图片+文本对话失败: " + e.getMessage());
        }
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    public Result<String> health() {
        return Result.ok("AI Cloud 服务运行正常");
    }
    

}

