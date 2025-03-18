package com.cathay.demo.util;

import com.cathay.demo.model.annotation.LogExecutionTime;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.exception.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Generic rest api util
 */
@Slf4j
@Service
public class ApiUtil {

    private final RestTemplate restTemplate;

    public ApiUtil(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @LogExecutionTime
    public <T> T sendGet(String url, Class<T> t) {
        HttpEntity<String> httpEntity = new HttpEntity<>(useDefaultHeader());
        return sendRequest(url, HttpMethod.GET, httpEntity, t);
    }

    @LogExecutionTime
    public <T> T sendGet(String url, Class<T> t, Map<String, String> queryData) {
        url = this.mappingQueryString(url, queryData);
        HttpEntity<String> httpEntity = new HttpEntity<>(useDefaultHeader());
        return sendRequest(url, HttpMethod.GET, httpEntity, t);
    }

    @LogExecutionTime
    public <T> T sendPost(String url, Class<T> t, Object source) {
        String json = ConvertUtil.toJson(source);
        HttpEntity<String> httpEntity = new HttpEntity<>(json, useDefaultHeader());
        return sendRequest(url, HttpMethod.POST, httpEntity, t);
    }

    @LogExecutionTime
    public <T> T sendPost(String url, HttpHeaders headers, Class<T> t, Object source) {
        String json = ConvertUtil.toJson(source);
        HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
        return sendRequest(url, HttpMethod.POST, httpEntity, t);
    }

    private HttpHeaders useDefaultHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String mappingQueryString(String url, Map<String, String> queryData) {
        StringBuilder queryString = new StringBuilder();
        log.info("ControllerMap > {}", queryData);

        for (Map.Entry<String, String> entry : queryData.entrySet()) {
            if (queryString.length() > 0) {
                queryString.append("&");
            }
            try {
                queryString.append(URLEncoder.encode(entry.getKey(), String.valueOf(StandardCharsets.UTF_8)))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), String.valueOf(StandardCharsets.UTF_8)));
            } catch (UnsupportedEncodingException e) {
                log.error("Build queryString error", e);
                throw new GenericException(RequestStatus.API_FAIL, "Build queryString error", e);
            }
        }
        return url + "?" + queryString.toString().replace("+", Character.toString((char) 32)).replace("%24", "$");
    }

    private <T> T sendRequest(String url, HttpMethod method, HttpEntity<?> httpEntity, Class<T> responseType) {
        log.info("Sending {} request to {}, Entity: {}", method, url, httpEntity);
        try { // 嘗試請求
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, method, httpEntity, responseType);
            log.info("Response status: {}", responseEntity.getStatusCode());

            // 若非 200
            if (!responseEntity.getStatusCode().is2xxSuccessful())
                throw new GenericException(RequestStatus.API_FAIL, "Response not successful.");

            log.info("Response Body: {}", responseEntity.getBody());
            return responseEntity.getBody();
        } catch (Exception e) { // 若是送出發生失敗
            log.error("Call Api Error: {}", String.valueOf(e));
            throw new GenericException(RequestStatus.API_FAIL, "Call Api Error.");
        }
    }
}
