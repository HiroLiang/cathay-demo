package com.cathay.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Coin Desk API 回傳物件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinDeskDto {

    private TimeDTO time;
    private String disclaimer;
    private String chartName;
    private Map<String, CurrencyDTO> bpi;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeDTO {
        private String updated;
        private String updatedISO;
        @JsonProperty("updateduk")
        private String updatedUK;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyDTO {
        private String code;
        private String symbol;
        private String rate;
        private String description;
        private double rateFloat;

        @JsonProperty("rate_float")
        public void setRateFloat(double rateFloat) {
            this.rateFloat = rateFloat;
        }
    }
}
