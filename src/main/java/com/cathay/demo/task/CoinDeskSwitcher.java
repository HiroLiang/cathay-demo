package com.cathay.demo.task;

import com.cathay.demo.db.entity.CurrencyNameContrast;
import com.cathay.demo.model.dto.CoinDeskDto;
import com.cathay.demo.model.dto.CoinDeskInfoDto;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.exception.GenericException;
import com.cathay.demo.model.part.Task;
import com.cathay.demo.service.CurrencyService;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class CoinDeskSwitcher implements Task<CoinDeskInfoDto> {

    private final CurrencyService currencyService;

    private final CoinDeskDto coinDeskDto;

    private final List<CurrencyNameContrast> contrasts = new ArrayList<>();

    private final CoinDeskInfoDto result = new CoinDeskInfoDto();

    public CoinDeskSwitcher(CurrencyService currencyService, CoinDeskDto coinDeskDto) {
        this.currencyService = currencyService;
        this.coinDeskDto = coinDeskDto;
    }

    @Override
    public void process() {
        if (this.coinDeskDto == null) throw new GenericException(RequestStatus.SYSTEM_ERROR, "Modify not allowed");

        processDate();

        initCurrencyContrast();
        processCurrency();
    }

    @Override
    public CoinDeskInfoDto getData() {
        return this.result;
    }

    public CoinDeskSwitcher formatRateToUSD() {
        processRate();
        return this;
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

    private void processRate() {
        List<CoinDeskInfoDto.CurrencyInfo> currencyInfo = this.result.getCurrencyInfo();

        CoinDeskInfoDto.CurrencyInfo usdInfo = null;

        for (CoinDeskInfoDto.CurrencyInfo currency : currencyInfo) {
            if (currency.getCode().equals("USD")) usdInfo = currency;
        }

        if (usdInfo == null) return;

        double usdRate = usdInfo.getRate();

        for (CoinDeskInfoDto.CurrencyInfo currency : currencyInfo) {
            BigDecimal rate = new BigDecimal(currency.getRate() / usdRate)
                    .setScale(10, RoundingMode.HALF_UP);
            currency.setRate(rate.doubleValue());
        }
    }

}
