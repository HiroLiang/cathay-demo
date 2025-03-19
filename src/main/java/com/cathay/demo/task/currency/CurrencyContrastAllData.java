package com.cathay.demo.task.currency;

import com.cathay.demo.db.entity.CurrencyNameContrast;
import com.cathay.demo.model.dto.CurrencyContrastDto;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.model.exception.GenericException;
import com.cathay.demo.service.currency.CurrencyService;
import com.cathay.demo.service.ServiceCollector;
import com.cathay.demo.task.StandardTask;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CurrencyContrastAllData extends StandardTask<List<CurrencyContrastDto>> {

    private final CurrencyService currencyService;

    private final List<CurrencyNameContrast> contrasts = new ArrayList<>();

    private final List<CurrencyContrastDto> result = new ArrayList<>();

    public CurrencyContrastAllData(ServiceCollector collector) {
        this.currencyService = collector.getCurrencyService();
    }

    @Override
    protected void doAction() {
        // 嘗試取得 DB 所有對照
        try {
            this.contrasts.addAll(currencyService.getAllContrasts());
            this.result.addAll(contrasts.stream()
                    .map(CurrencyContrastDto::new)
                    .collect(Collectors.toList()));

            // 可能為 Response
            storeResponse(RequestStatus.OK, result);
        } catch (Exception e) {
            log.error("Get All Contrasts failed", e);
            // 若是查無資料，依舊拋出
            if (e instanceof GenericException) throw (GenericException) e;

            // 若是 DB 異常，任務失敗，並記錄 Response
            forceFailed();
            storeResponse(RequestStatus.DB_FAIL, result);
        }

        // 儲存查詢結果入 Store
        createStore(TaskTag.CURRENCY_CONTRAST_DATA_ALL, contrasts);

    }

    @Override
    public List<CurrencyContrastDto> getData() {
        return result;
    }
}
