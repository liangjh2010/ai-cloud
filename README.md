# AI Cloud 服务

## 项目简介

AI Cloud 是一个基于 Spring AI 1.0.3 的 Ollama 调用服务，为 aika_server 提供本地大模型支持。

### 核心功能

1. **文本对话**：纯文本对话功能（用于 AI 对话、AI 点歌解析）✅
2. **图片识别**：图片+文本多模态功能（用于场景识别）✅
3. **语音识别**：Whisper 语音转文字功能 ✅
4. **统一接口**：提供 RESTful API 供 aika_server 通过 OpenFeign 调用

### 技术栈

- **JDK**: 21
- **Spring Boot**: 3.2.7
- **Spring AI**: 1.0.0
- **Knife4j**: 4.4.0
- **Ollama 模型**: qwen2.5vl:3b
- **Whisper**: whisper.cpp 语音识别服务

---

## 快速开始

### 1. 环境要求

- JDK 21
- Maven 3.6+
- Ollama 服务（已部署在 http://35.221.238.240:11434）
- Whisper 服务（可选，用于语音识别，默认 http://localhost:8083）

### 2. 配置文件

配置文件位置：`src/main/resources/application.yml`

```yaml
server:
  port: 8082

spring:
  ai:
    ollama:
      base-url: http://35.221.238.240:11434
      chat:
        options:
          model: qwen2.5vl:3b

# Whisper 语音识别配置
whisper:
  server:
    url: http://localhost:8083
    inference-path: /inference
    timeout: 60000
```

### 3. 启动项目

```bash
# 编译项目
mvn clean install

# 启动服务
mvn spring-boot:run
```

或在 IDEA 中直接运行 `AiCloudApplication.java`

### 4. 访问 API 文档

启动成功后，访问以下地址：
- **Knife4j 文档界面**：http://localhost:8082/doc.html
- **API Docs JSON**：http://localhost:8082/v3/api-docs

---

## API 接口

### 1. 文本对话

**接口地址**：`POST /api/ollama/chat`

**请求参数**：
```json
{
  "prompt": "你好，请介绍一下你自己"
}
```

**响应示例**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "response": "你好！我是AIKA智能音箱助手...",
    "modelName": "qwen2.5vl:3b",
    "responseTime": 1200
  }
}
```

### 2. 图片+文本对话

**接口地址**：`POST /api/ollama/chat-with-image`

**请求参数**：
```json
{
  "prompt": "请描述这张图片中的场景",
  "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
  "imageUrl": "https://example.com/image.jpg"
}
```

**参数说明**：
- `prompt`：提示词（必填）
- `imageBase64`：图片 Base64 编码（优先使用）
- `imageUrl`：图片 URL（当 imageBase64 为空时使用）
- **优先级**：imageBase64 > imageUrl，至少需要提供其中一个

**响应示例**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "response": "这是一个生日派对场景，有蛋糕、气球和人物",
    "modelName": "qwen2.5vl:3b",
    "responseTime": 3500
  }
}
```

### 3. 健康检查

**接口地址**：`GET /api/ollama/health`

**响应示例**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "AI Cloud 服务运行正常"
}
```

---

## Whisper 语音识别接口

### 1. 语音识别（Base64）

**接口地址**：`POST /api/whisper/recognize`

**请求参数**：
```json
{
  "audioBase64": "UklGRiQAAABXQVZFZm10...",
  "language": "zh",
  "responseFormat": "json",
  "translate": false
}
```

**参数说明**：
- `audioBase64`：音频文件 Base64 编码（必填）
- `language`：语言代码，zh=中文，en=英文，auto=自动检测（默认：zh）
- `responseFormat`：响应格式 json/text/verbose_json（默认：json）
- `translate`：是否翻译成英文（默认：false）

**响应示例**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "text": "你好，我想唱周杰伦的《稻香》",
    "language": "zh",
    "processingTime": 2500,
    "success": true,
    "errorMessage": null
  }
}
```

### 2. 语音识别（文件上传）

**接口地址**：`POST /api/whisper/recognize-file`

**请求参数**（multipart/form-data）：
- `file`：音频文件（支持 mp3、wav、m4a 等格式）
- `language`：语言代码（可选，默认：zh）

**响应示例**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "text": "今天天气真好",
    "language": "zh",
    "processingTime": 1800,
    "success": true,
    "errorMessage": null
  }
}
```

### 3. Whisper 健康检查

**接口地址**：`GET /api/whisper/health`

**响应示例**：
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "Whisper 语音识别服务运行正常"
}
```

---

## 与 aika_server 集成

### 配置说明

aika_server 通过 OpenFeign 调用 ai-cloud 服务。

在 `aika_server/proj-device-api/src/main/resources/application.yml` 中配置：

```yaml
ai:
  provider:
    # 切换到 ollama 使用 ai-cloud 服务
    scene-recognition: ollama  # 或 baidu
    chat: ollama               # 或 qianwen
    song-parse: ollama         # 或 google
  
  aika-ai:
    base-url: http://localhost:8082
    timeout: 300
```

### Feign 客户端

aika_server 中的 `AikaAiFeignClient` 已配置完成，无需额外修改。

---

## 项目结构

```
ai-cloud/
├── src/main/java/com/proj/ai/
│   ├── AiCloudApplication.java          # 启动类
│   ├── client/
│   │   └── OllamaClient.java           # Ollama 客户端
│   ├── common/
│   │   └── Result.java                 # 统一返回结果
│   ├── config/
│   │   ├── SwaggerConfig.java          # Swagger 配置
│   │   └── WhisperConfig.java          # Whisper 配置
│   ├── controller/
│   │   ├── OllamaController.java       # Ollama 控制器
│   │   └── WhisperController.java      # Whisper 控制器
│   ├── dto/
│   │   ├── OllamaChatRequest.java      # Ollama 文本请求
│   │   ├── OllamaChatWithImageRequest.java  # Ollama 图片+文本请求
│   │   ├── OllamaResponse.java         # Ollama 响应
│   │   ├── WhisperRequest.java         # Whisper 请求
│   │   └── WhisperResponse.java        # Whisper 响应
│   ├── exception/
│   │   └── GlobalExceptionHandler.java # 全局异常处理
│   └── service/
│       ├── OllamaService.java          # Ollama 服务接口
│       ├── WhisperService.java         # Whisper 服务接口
│       └── impl/
│           ├── OllamaServiceImpl.java  # Ollama 服务实现
│           └── WhisperServiceImpl.java # Whisper 服务实现
└── src/main/resources/
    └── application.yml                 # 配置文件
```

---

## 注意事项

1. **图片格式**：支持 Base64 编码的图片，会自动清理 `data:image/jpeg;base64,` 前缀
2. **响应时间**：大模型响应较慢，建议设置合理的超时时间（300秒）
3. **日志级别**：开发环境使用 `debug`，生产环境建议使用 `info`
4. **端口冲突**：确保 8082 端口未被占用
5. **音频格式**：Whisper 支持 mp3、wav、m4a 等常见音频格式
6. **文件大小**：默认支持最大 50MB 的音频文件上传
7. **Whisper 服务**：确保 Whisper 服务已启动并可访问（默认 http://localhost:8083）

---


