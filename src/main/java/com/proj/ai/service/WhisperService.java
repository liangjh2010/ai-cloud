package com.proj.ai.service;

import com.proj.ai.dto.WhisperRequest;
import com.proj.ai.dto.WhisperResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Whisper 语音识别服务接口
 */
public interface WhisperService {
    
    /**
     * 语音识别（Base64 编码）
     *
     * @param request 请求参数
     * @return 识别结果
     */
    WhisperResponse recognize(WhisperRequest request);
    
    /**
     * 语音识别（文件上传）
     *
     * @param file 音频文件
     * @param language 语言代码（可选，默认 zh）
     * @return 识别结果
     */
    WhisperResponse recognizeFile(MultipartFile file, String language);
}

