package org.example.wyspring.service.ocr;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.wyspring.config.BaiduOcrProperties;
import org.example.wyspring.enums.ErrorCode;
import org.example.wyspring.exception.BusinessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.StringJoiner;

/**
 * 百度通用文字识别（高精度版 general_basic 足够多数试卷场景）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BaiduOcrClient {

    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String OCR_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";

    private final RestTemplate restTemplate;
    private final BaiduOcrProperties properties;

    private volatile String cachedAccessToken;
    private volatile long tokenExpireAtMillis;

    /**
     * 将试卷图片识别为纯文本（多行）
     */
    public String recognizeText(byte[] imageBytes) {
        ensureConfigured();
        String token = getAccessToken();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        String url = UriComponentsBuilder.fromHttpUrl(OCR_URL)
                .queryParam("access_token", token)
                .build(true)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image", base64);
        body.add("language_type", "CHN_ENG");
        body.add("detect_direction", "true");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        String resp = restTemplate.postForObject(url, entity, String.class);
        if (StringUtils.isBlank(resp)) {
            throw new BusinessException(ErrorCode.OCR_PROCESSING_ERROR, "OCR 返回为空");
        }

        JSONObject json = JSON.parseObject(resp);
        if (json.containsKey("error_code")) {
            int code = json.getIntValue("error_code");
            String msg = json.getString("error_msg");
            log.warn("百度 OCR 错误: {} {}", code, msg);
            throw new BusinessException(ErrorCode.OCR_PROCESSING_ERROR,
                    "百度 OCR 失败: " + msg + " (code=" + code + ")");
        }

        JSONArray wordsResult = json.getJSONArray("words_result");
        if (wordsResult == null || wordsResult.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner("\n");
        for (int i = 0; i < wordsResult.size(); i++) {
            JSONObject line = wordsResult.getJSONObject(i);
            String w = line.getString("words");
            if (StringUtils.isNotBlank(w)) {
                joiner.add(w.trim());
            }
        }
        return joiner.toString();
    }

    private void ensureConfigured() {
        if (!properties.isEnabled()) {
            throw new BusinessException(ErrorCode.OCR_PROCESSING_ERROR,
                    "未启用 OCR：请在 application.yml 中设置 baidu.ocr.enabled=true 并配置 api-key、secret-key");
        }
        if (StringUtils.isBlank(properties.getApiKey()) || StringUtils.isBlank(properties.getSecretKey())) {
            throw new BusinessException(ErrorCode.OCR_PROCESSING_ERROR,
                    "未配置百度 OCR：请设置 baidu.ocr.api-key 与 baidu.ocr.secret-key（百度智能云控制台）");
        }
    }

    private String getAccessToken() {
        long now = System.currentTimeMillis();
        if (cachedAccessToken != null && now < tokenExpireAtMillis - 120_000) {
            return cachedAccessToken;
        }
        synchronized (this) {
            if (cachedAccessToken != null && now < tokenExpireAtMillis - 120_000) {
                return cachedAccessToken;
            }
            String url = UriComponentsBuilder.fromHttpUrl(TOKEN_URL)
                    .queryParam("grant_type", "client_credentials")
                    .queryParam("client_id", properties.getApiKey())
                    .queryParam("client_secret", properties.getSecretKey())
                    .build(true)
                    .toUriString();

            String resp = restTemplate.getForObject(url, String.class);
            if (StringUtils.isBlank(resp)) {
                throw new BusinessException(ErrorCode.OCR_PROCESSING_ERROR, "获取百度 access_token 失败");
            }
            JSONObject json = JSON.parseObject(resp);
            if (json.containsKey("error")) {
                throw new BusinessException(ErrorCode.OCR_PROCESSING_ERROR,
                        "获取 token 失败: " + json.getString("error_description"));
            }
            cachedAccessToken = json.getString("access_token");
            int expiresIn = json.getIntValue("expires_in");
            if (expiresIn <= 0) {
                expiresIn = 2592000;
            }
            tokenExpireAtMillis = now + expiresIn * 1000L;
            return cachedAccessToken;
        }
    }
}
