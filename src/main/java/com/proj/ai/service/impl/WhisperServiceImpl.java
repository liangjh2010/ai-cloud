package com.proj.ai.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.proj.ai.config.WhisperConfig;
import com.proj.ai.dto.WhisperRequest;
import com.proj.ai.dto.WhisperResponse;
import com.proj.ai.service.WhisperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

/**
 * Whisper 语音识别服务实现
 */
@Slf4j
@Service
public class WhisperServiceImpl implements WhisperService {
    
    @Autowired
    private WhisperConfig whisperConfig;
    
    private final RestTemplate restTemplate;
    
    public WhisperServiceImpl() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * 语音识别（Base64 编码）
     */
    @Override
    public WhisperResponse recognize(WhisperRequest request) {
        log.info("开始处理语音识别请求，语言: {}", request.getLanguage());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Base64 解码
            byte[] audioBytes = Base64.getDecoder().decode(request.getAudioBase64());
            
            // 调用 Whisper 服务
            String resultText = callWhisperService(
                    audioBytes, 
                    request.getLanguage(), 
                    request.getResponseFormat()
            );
            
            long endTime = System.currentTimeMillis();
            
            log.info("语音识别成功，耗时: {}ms", endTime - startTime);
            
            // 构建响应
            return WhisperResponse.builder()
                    .text(resultText)
                    .language(request.getLanguage())
                    .processingTime(endTime - startTime)
                    .success(true)
                    .build();
                    
        } catch (Exception e) {
            log.error("语音识别失败: {}", e.getMessage(), e);
            
            long endTime = System.currentTimeMillis();
            
            return WhisperResponse.builder()
                    .success(false)
                    .errorMessage("语音识别失败: " + e.getMessage())
                    .processingTime(endTime - startTime)
                    .build();
        }
    }
    
    /**
     * 语音识别（文件上传）
     */
    @Override
    public WhisperResponse recognizeFile(MultipartFile file, String language) {
        log.info("开始处理文件上传识别请求，文件名: {}, 语言: {}", file.getOriginalFilename(), language);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 获取文件字节
            byte[] audioBytes = file.getBytes();
            
            // 使用默认语言
            String lang = StringUtils.hasText(language) ? language : "zh";
            
            // 调用 Whisper 服务
            String resultText = callWhisperService(audioBytes, lang, "json");
            
            long endTime = System.currentTimeMillis();
            
            log.info("语音识别成功，耗时: {}ms", endTime - startTime);
            
            // 构建响应
            return WhisperResponse.builder()
                    .text(resultText)
                    .language(lang)
                    .processingTime(endTime - startTime)
                    .success(true)
                    .build();
                    
        } catch (Exception e) {
            log.error("语音识别失败: {}", e.getMessage(), e);
            
            long endTime = System.currentTimeMillis();
            
            return WhisperResponse.builder()
                    .success(false)
                    .errorMessage("语音识别失败: " + e.getMessage())
                    .processingTime(endTime - startTime)
                    .build();
        }
    }
    
    /**
     * 调用 Whisper 服务
     *
     * @param audioBytes 音频字节数组
     * @param language 语言代码
     * @param responseFormat 响应格式
     * @return 识别的文本
     */
    private String callWhisperService(byte[] audioBytes, String language, String responseFormat) {
        try {
            // 构建 multipart 请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // 添加文件
            ByteArrayResource fileResource = new ByteArrayResource(audioBytes) {
                @Override
                public String getFilename() {
                    return "audio.wav";
                }
            };
            body.add("file", fileResource);
            
            // 添加语言参数
            if (StringUtils.hasText(language)) {
                body.add("language", language);
            }
            
            // 添加响应格式
            if (StringUtils.hasText(responseFormat)) {
                body.add("response_format", responseFormat);
            }
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // 发送请求
            String url = whisperConfig.getInferenceUrl();
            log.info("调用 Whisper 服务: {}", url);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            
            // 解析响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 尝试解析 JSON
                try {
                    JSONObject jsonResponse = JSON.parseObject(response.getBody());
                    return jsonResponse.getString("text");
                } catch (Exception e) {
                    // 如果不是 JSON，直接返回文本
                    return response.getBody();
                }
            } else {
                throw new RuntimeException("Whisper 服务返回错误: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("调用 Whisper 服务失败: {}", e.getMessage(), e);
            throw new RuntimeException("调用 Whisper 服务失败: " + e.getMessage(), e);
        }
    }
}

