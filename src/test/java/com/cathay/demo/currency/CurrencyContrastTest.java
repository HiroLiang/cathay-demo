package com.cathay.demo.currency;

import com.cathay.demo.model.dto.BaseRs;
import com.cathay.demo.model.dto.CurrencyContrastDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

/**
 * 幣別中文對照 CRUD 測試:
 * 1. 用 SQL 預填資料測試
 * 2. 統一 Rs 外層型別
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CurrencyContrastTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testGetAll() {
        log.info("Testing GetContrast All API");

        String url = "http://localhost:" + port + "/currency/get-all/contrast";
        ResponseEntity<BaseRs<List<CurrencyContrastDto>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseRs<List<CurrencyContrastDto>>>() {}
        );

        checkResponse(response);

        List<CurrencyContrastDto> contrasts = Objects.requireNonNull(response.getBody()).getContain();

        assertThat(contrasts).isNotNull();
        assertThat(contrasts.size()).isEqualTo(4);

        log.info("Get all contrasts: {} \n", contrasts);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testGetContrast() {
        String usd = "USD";
        log.info("Testing GetContrast: {} API", usd);

        String url = "http://localhost:" + port + "/currency/get/contrast/" + usd;

        ResponseEntity<BaseRs<CurrencyContrastDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseRs<CurrencyContrastDto>>() {}
        );

        checkResponse(response);

        CurrencyContrastDto contrast = Objects.requireNonNull(response.getBody()).getContain();

        assertThat(contrast).isNotNull();
        assertThat(contrast.getCode()).isEqualTo(usd);

        log.info("Get contrast: {} \n", contrast);
    }

    @Test
    @Sql("/schema.sql")
    void testAddContrast() {
        String url = "http://localhost:" + port + "/currency/add/contrast";
        CurrencyContrastDto rq = new CurrencyContrastDto("USD", "United States Dollar", "美元");

        log.info("Testing AddContrast: {} API", rq);

        ResponseEntity<BaseRs<CurrencyContrastDto>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(rq),
                new ParameterizedTypeReference<BaseRs<CurrencyContrastDto>>() {}
        );

        checkResponse(response);

        CurrencyContrastDto dto = Objects.requireNonNull(response.getBody()).getContain();

        assertThat(dto).isNotNull();
        assertThat(dto.getCode()).isEqualTo("USD");
        assertThat(dto.getDescription()).isEqualTo("United States Dollar");

        log.info("Add contrast: {} \n", dto);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testUpdateContrast() {
        String url = "http://localhost:" + port + "/currency/update/contrast";
        CurrencyContrastDto rq = new CurrencyContrastDto("USD", "Japanese yen", "日元");
        log.info("Testing UpdateContrast: {} API", rq);

        ResponseEntity<BaseRs<CurrencyContrastDto>> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(rq),
                new ParameterizedTypeReference<BaseRs<CurrencyContrastDto>>() {}
        );

        checkResponse(response);

        CurrencyContrastDto dto = Objects.requireNonNull(response.getBody()).getContain();

        assertThat(dto).isNotNull();
        assertThat(dto.getCode()).isEqualTo("USD");
        assertThat(dto.getDescription()).isEqualTo("Japanese yen");
        assertThat(dto.getChineseName()).isEqualTo("日元");

        log.info("Update contrast: {} \n", dto);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testDeleteContrast() {
        String usd = "USD";
        log.info("Testing DeleteContrast: {} API", usd);

        String url = "http://localhost:" + port + "/currency/delete/contrast/" + usd;
        ResponseEntity<BaseRs<CurrencyContrastDto>> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<BaseRs<CurrencyContrastDto>>() {}
        );

        checkResponse(response);

        String checkUrl = "http://localhost:" + port + "/currency/get-all/contrast";
        ResponseEntity<BaseRs<List<CurrencyContrastDto>>> check = restTemplate.exchange(
                checkUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseRs<List<CurrencyContrastDto>>>() {}
        );

        checkResponse(check);

        List<CurrencyContrastDto> contrasts = Objects.requireNonNull(check.getBody()).getContain();

        assertThat(contrasts).isNotNull();
        assertThat(contrasts.size()).isEqualTo(3);

        log.info("Delete contrast... End \n");
    }

    private <T> void checkResponse(ResponseEntity<BaseRs<T>> response) {
        log.info("Get response: {}", response);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        BaseRs<?> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getCode()).isEqualTo("0000");
    }
}
