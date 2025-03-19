package com.cathay.demo.task.currency;

import com.cathay.demo.db.entity.CurrencyNameContrast;
import com.cathay.demo.model.dto.CoinDeskDto;
import com.cathay.demo.model.dto.CoinDeskInfoDto;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.exception.GenericException;
import com.cathay.demo.service.CurrencyService;
import com.cathay.demo.task.StandardTask;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 轉換器：
 * 1. 實作 Task 讓 class 被執行時可用 Processor 包交易
 */
@Slf4j
public class CoinDeskSwitcher extends StandardTask<CoinDeskInfoDto> {

    private final CurrencyService currencyService;

    private final CoinDeskDto coinDeskDto;

    private final List<CurrencyNameContrast> contrasts = new ArrayList<>();

    private final CoinDeskInfoDto result = new CoinDeskInfoDto();

    public CoinDeskSwitcher(CurrencyService currencyService, CoinDeskDto coinDeskDto) {
        this.currencyService = currencyService;
        this.coinDeskDto = coinDeskDto;
    }

    @Override
    protected void doAction() {
        if (this.coinDeskDto == null) throw new GenericException(RequestStatus.SYSTEM_ERROR, "Modify not allowed");

        log.info("Processing CoinDesk Switcher");
        processDate();

        initCurrencyContrast();
        processCurrency();
    }

    @Override
    public CoinDeskInfoDto getData() {
        return this.result;
    }

    private void processDate() {
        String isoTime = this.coinDeskDto.getTime().getUpdatedISO();
        ZonedDateTime utcTime = ZonedDateTime.parse(isoTime, DateTimeFormatter.ISO_DATE_TIME);
        ZonedDateTime localTime = utcTime.withZoneSameInstant(ZoneId.systemDefault());

        this.result.setUpdateTime(localTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

    }

    private void processCurrency() {
        CurrencyNameContrast baseCurrency = getContrastByDesc(this.coinDeskDto.getChartName());
        this.result.getCurrencyInfo().add(new CoinDeskInfoDto.CurrencyInfo(baseCurrency.getCode(),
                        baseCurrency.getDescription(), baseCurrency.getChineseName(), 1));
        if (this.coinDeskDto.getBpi() != null) {
            Map<String, CoinDeskDto.CurrencyDTO> bpi = this.coinDeskDto.getBpi();
            for (String key : bpi.keySet()) {
                CoinDeskDto.CurrencyDTO currency = bpi.get(key);
                CurrencyNameContrast contrast = getContrastByCode(currency.getCode());
                this.result.getCurrencyInfo().add(new CoinDeskInfoDto.CurrencyInfo(contrast.getCode(),
                        contrast.getDescription(), contrast.getChineseName(), currency.getRateFloat()));
            }
        }
    }

    private void initCurrencyContrast() {
        this.contrasts.clear();
        this.contrasts.addAll(currencyService.getAllContrasts());
    }

    private CurrencyNameContrast getContrastByCode(String code) {
        for (CurrencyNameContrast contrast : contrasts) {
            if (contrast.getCode().equals(code)) return contrast;
        }
        throw new GenericException(RequestStatus.DB_FAIL, "Data not found");
    }

    private CurrencyNameContrast getContrastByDesc(String description) {
        for (CurrencyNameContrast contrast : contrasts) {
            if (contrast.getDescription().equals(description)) return contrast;
        }
        throw new GenericException(RequestStatus.DB_FAIL, "Data not found");
    }

}
