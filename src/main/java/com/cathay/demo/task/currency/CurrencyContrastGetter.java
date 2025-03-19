package com.cathay.demo.task.currency;

import com.cathay.demo.model.dto.CurrencyContrastDto;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.model.exception.GenericException;
import com.cathay.demo.service.currency.CurrencyService;
import com.cathay.demo.service.ServiceCollector;
import com.cathay.demo.task.StandardTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得 Code 對應主表資料
 */
@Slf4j
public class CurrencyContrastGetter extends StandardTask<CurrencyContrastDto> {

    private final CurrencyService currencyService;

    private String code = null;

    private CurrencyContrastDto result = null;

    public CurrencyContrastGetter(ServiceCollector collector) {
        this.currencyService = collector.getCurrencyService();
    }

    public CurrencyContrastGetter setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    protected void doAction() {
        // 若 Code 未設定，拋異常
        if (this.code == null) throw new GenericException(RequestStatus.DB_FAIL, "Search code is null");

        // 嘗試取得 DB 資料
        try {
            this.result = new CurrencyContrastDto(currencyService.getContrastByCode(code));
            createStore(TaskTag.RS_STATUS, RequestStatus.OK);

            // 可能為 Response
            storeResponse(RequestStatus.OK, result);
        } catch (Exception e) {
            log.warn("Code: {} not found data", code, e);
            // 若是查無資料，依舊拋出
            if (e instanceof GenericException) throw (GenericException) e;

            // 若是 DB 異常，任務失敗，並記錄 Response
            forceFailed();
            storeResponse(RequestStatus.DB_FAIL, result);
        }
    }

    @Override
    public CurrencyContrastDto getData() {
        return result;
    }
}
