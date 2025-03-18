package com.cathay.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CoinDeskDto {

    private TimeDTO time;
    private String disclaimer;
    private String chartName;
    private Map<String, CurrencyDTO> bpi;

    @Data
    public static class TimeDTO {
        private String updated;
        private String updatedISO;
        @JsonProperty("updateduk")
        private String updatedUK;
    }

    @Data
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
