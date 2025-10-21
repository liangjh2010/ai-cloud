package com.proj.ai.controller;

import com.proj.ai.common.Result;
import com.proj.ai.dto.WhisperRequest;
import com.proj.ai.dto.WhisperResponse;
import com.proj.ai.service.WhisperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Whisper 语音识别接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/whisper")
@Tag(name = "Whisper 语音识别接口", description = "Whisper 语音转文字接口")
@CrossOrigin(origins = "*")
public class WhisperController {
    
    @Autowired
    private WhisperService whisperService;
    
    /**
     * 语音识别接口（Base64）
     */
    @PostMapping("/recognize")
    @Operation(summary = "语音识别（Base64）", description = "通过 Base64 编码的音频进行语音识别")
    public Result<WhisperResponse> recognize(@Valid @RequestBody WhisperRequest request) {
        try {
            // 打印入参（Base64 简化显示）
            String audioInfo = request.getAudioBase64() != null 
                ? "Base64(长度:" + request.getAudioBase64().length() + ")" 
                : "null";
            log.info("[语音识别] 入参 -> audio: {}, language: {}", audioInfo, request.getLanguage());
            
            // 调用服务
            WhisperResponse response = whisperService.recognize(request);
            
            // 打印出参
            if (response.getSuccess()) {
                log.info("[语音识别] 出参 -> text: {}, processingTime: {}ms", 
                    response.getText(), response.getProcessingTime());
                return Result.ok(response);
            } else {
                log.error("[语音识别] 失败 -> error: {}", response.getErrorMessage());
                return Result.error(response.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("[语音识别] 异常 -> {}", e.getMessage(), e);
            return Result.error("语音识别失败: " + e.getMessage());
        }
    }
    
    /**
     * 语音识别接口（文件上传）
     */
    @PostMapping("/recognize-file")
    @Operation(summary = "语音识别（文件上传）", description = "通过上传音频文件进行语音识别（支持 mp3、wav、m4a 等格式）")
    public Result<WhisperResponse> recognizeFile(
            @Parameter(description = "音频文件", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "语言代码（zh=中文, en=英文, auto=自动检测）", example = "zh")
            @RequestParam(value = "language", required = false, defaultValue = "zh") String language) {
        
        try {
            // 打印入参
            log.info("[语音识别-文件] 入参 -> fileName: {}, fileSize: {} bytes, language: {}", 
                file.getOriginalFilename(), file.getSize(), language);
            
            // 调用服务
            WhisperResponse response = whisperService.recognizeFile(file, language);
            
            // 打印出参
            if (response.getSuccess()) {
                log.info("[语音识别-文件] 出参 -> text: {}, processingTime: {}ms", 
                    response.getText(), response.getProcessingTime());
                return Result.ok(response);
            } else {
                log.error("[语音识别-文件] 失败 -> error: {}", response.getErrorMessage());
                return Result.error(response.getErrorMessage());
            }
            
        } catch (Exception e) {
            log.error("[语音识别-文件] 异常 -> {}", e.getMessage(), e);
            return Result.error("语音识别失败: " + e.getMessage());
        }
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查 Whisper 服务是否正常")
    public Result<String> health() {
        return Result.ok("Whisper 语音识别服务运行正常");
    }
}

