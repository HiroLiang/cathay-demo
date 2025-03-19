package com.cathay.demo.task.currency;

import com.cathay.demo.db.entity.CurrencyNameContrast;
import com.cathay.demo.model.dto.CoinDeskDto;
import com.cathay.demo.model.dto.CoinDeskInfoDto;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.model.exception.GenericException;
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

    private CoinDeskDto coinDeskDto;

    private final List<CurrencyNameContrast> contrasts = new ArrayList<>();

    private final CoinDeskInfoDto result = new CoinDeskInfoDto();

    @Override
    @SuppressWarnings("unchecked")
    protected void doAction() {
        // 取得 Store 中的 Response
        this.coinDeskDto = receive(TaskTag.COIN_DESK_RESPONSE, CoinDeskDto.class);

        // 取得 Store 中 Currency 中文對照表
        this.contrasts.addAll(receive(TaskTag.CURRENCY_CONTRAST_DATA_ALL, List.class));

        // 若找不到 (失敗或沒打)
        if (this.coinDeskDto == null) throw new GenericException(RequestStatus.SYSTEM_ERROR, "Response not found.");

        // 解析更新日期
        processDate();

        // 解析 Coin Desk 回應中所有幣別與匯率
        processCurrency();

        // 可能為 Response
        storeResponse(RequestStatus.OK, result);
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

        if (baseCurrency != null) {
            this.result.getCurrencyInfo().add(new CoinDeskInfoDto.CurrencyInfo(baseCurrency.getCode(),
                    baseCurrency.getDescription(), baseCurrency.getChineseName(), 1));
        } else {
            this.result.getCurrencyInfo().add(new CoinDeskInfoDto.CurrencyInfo("", this.coinDeskDto.getChartName(),
                    "", 1));
        }

        if (this.coinDeskDto.getBpi() != null) {
            Map<String, CoinDeskDto.CurrencyDTO> bpi = this.coinDeskDto.getBpi();
            for (String key : bpi.keySet()) {
                CoinDeskDto.CurrencyDTO currency = bpi.get(key);
                CurrencyNameContrast contrast = getContrastByCode(currency.getCode());
                if (contrast != null) {
                    this.result.getCurrencyInfo().add(new CoinDeskInfoDto.CurrencyInfo(contrast.getCode(),
                            contrast.getDescription(), contrast.getChineseName(), currency.getRateFloat()));
                } else {
                    this.result.getCurrencyInfo().add(new CoinDeskInfoDto.CurrencyInfo(currency.getCode(),
                            currency.getDescription(), "", currency.getRateFloat()));
                }
            }
        }
    }

    private CurrencyNameContrast getContrastByCode(String code) {
        for (CurrencyNameContrast contrast : contrasts) {
            if (contrast.getCode().equals(code)) return contrast;
        }
        return null;
    }

    private CurrencyNameContrast getContrastByDesc(String description) {
        for (CurrencyNameContrast contrast : contrasts) {
            if (contrast.getDescription().equals(description)) return contrast;
        }
        return null;
    }

}
