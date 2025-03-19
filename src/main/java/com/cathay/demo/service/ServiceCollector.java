package com.cathay.demo.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service 集束：
 * 1. 避免實例化時循環依賴
 * 2. collector > logical-service > DB-service > repository
 */
@Service
@Getter
@RequiredArgsConstructor
public class ServiceCollector {

    private final CurrencyService currencyService;

    private final CoinDeskService coinDeskService;

}
