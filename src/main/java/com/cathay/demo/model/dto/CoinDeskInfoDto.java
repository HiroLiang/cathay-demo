package com.cathay.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Coin Desk 轉換後回傳物件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinDeskInfoDto {

    private String updateTime;

    private List<CurrencyInfo> currencyInfo = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurrencyInfo {
        private String code;
        private String description;
        private String chineseName;
        private double rate;
    }
}
