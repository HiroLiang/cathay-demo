package com.cathay.demo.api;

import com.cathay.demo.model.dto.BaseRs;
import com.cathay.demo.model.dto.CoinDeskDto;
import com.cathay.demo.model.dto.CoinDeskInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CoinDeskTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    void testCoinDeskApi() {
        log.info("Testing CoinDesk API");

        String url = "http://localhost:" + port + "/coin-desk/call-api";
        ResponseEntity<BaseRs<CoinDeskDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseRs<CoinDeskDto>>() {}
        );

        checkResponse(response);

        CoinDeskDto dto = Objects.requireNonNull(response.getBody()).getContain();

        assertThat(dto).isNotNull();
        assertThat(dto.getChartName()).isEqualTo("Bitcoin");

        log.info("Testing CoinDesk API response: {} \n", dto);
    }

    @Test
    @Sql({"/schema.sql", "/data.sql"})
    void testCoinDeskInfo() {
        log.info("Testing CoinDeskInfo API");

        String url = "http://localhost:" + port + "/coin-desk/get-info";
        ResponseEntity<BaseRs<CoinDeskInfoDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BaseRs<CoinDeskInfoDto>>() {}
        );

        checkResponse(response);

        CoinDeskInfoDto dto = Objects.requireNonNull(response.getBody()).getContain();

        assertThat(dto).isNotNull();
        assertThat(dto.getUpdateTime()).isEqualTo("2024/09/02 15:07:20");
        assertThat(dto.getCurrencyInfo().size()).isEqualTo(4);

        log.info("Testing CoinDeskInfo API response: {} \n", dto);

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
