package com.cathay.demo.task.currency;

import com.cathay.demo.cache.CurrencyContrastCache;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.task.StandardTask;

public class CurrencyContrastCacheAsData extends StandardTask<Void> {

    private final Purpose purpose;

    private String code;

    public CurrencyContrastCacheAsData(CurrencyContrastCacheAsData.Purpose purpose) {
        this.purpose = purpose;
    }

    public CurrencyContrastCacheAsData setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    protected void doAction() {
        alert();

        switch (purpose) {
            case ALL:
                createStore(TaskTag.CURRENCY_CONTRAST_DATA_ALL, CurrencyContrastCache.getAllCurrency());
                break;
            case CODE:
                if (code == null) break;
                createStore(TaskTag.CURRENCY_CONTRAST_DATA, CurrencyContrastCache.getCurrency(this.code));
                break;
        }
    }

    @Override
    public Void getData() {
        return null;
    }

    public enum Purpose {
        ALL,
        CODE
    }

    private void alert() {
        // TODO 警示管理者，DB I/O 異常
    }
}
