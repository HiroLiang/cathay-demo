package com.cathay.demo.task.currency;

import com.cathay.demo.model.dto.CurrencyContrastDto;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.model.exception.GenericException;
import com.cathay.demo.service.CurrencyService;
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
        } catch (Exception e) {
            // 若失敗，設置異常
            log.warn("Code: {} not found data", code, e);
            createStore(TaskTag.RS_STATUS, RequestStatus.DB_FAIL);
        }

        createStore(TaskTag.RS_OBJECT, result);
    }

    @Override
    public CurrencyContrastDto getData() {
        return result;
    }
}
