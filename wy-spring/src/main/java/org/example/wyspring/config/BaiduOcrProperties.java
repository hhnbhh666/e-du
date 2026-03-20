package org.example.wyspring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 百度 OCR（通用文字识别）配置。
 * 在 <a href="https://console.bce.baidu.com/ai/">百度智能云</a> 创建应用后填写 API Key 与 Secret Key。
 */
@Data
@Component
@ConfigurationProperties(prefix = "baidu.ocr")
public class BaiduOcrProperties {

    /**
     * 是否启用真实 OCR（需配置 api-key / secret-key）
     */
    private boolean enabled = false;

    private String apiKey = "";

    private String secretKey = "";
}
