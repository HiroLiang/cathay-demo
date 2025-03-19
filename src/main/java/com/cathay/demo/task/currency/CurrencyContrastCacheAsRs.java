package com.cathay.demo.task.currency;

import com.cathay.demo.cache.CurrencyContrastCache;
import com.cathay.demo.db.entity.CurrencyNameContrast;
import com.cathay.demo.model.dto.CurrencyContrastDto;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.task.StandardTask;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 將 Cache 資料依據需求填入 Rs object 中，推薦用 doIf 判斷是否需要
 */
public class CurrencyContrastCacheAsRs extends StandardTask<Void> {

    private final Purpose purpose;

    private String code;

    public CurrencyContrastCacheAsRs(Purpose purpose) {
        this.purpose = purpose;
    }

    public CurrencyContrastCacheAsRs setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    protected void doAction() {
        alert();

        switch (purpose) {
            case ALL:
                List<CurrencyNameContrast> contrasts = CurrencyContrastCache.getAllCurrency();
                createStore(TaskTag.RS_OBJECT, contrasts.stream()
                        .map(CurrencyContrastDto::new)
                        .collect(Collectors.toList()));
                break;
            case CODE:
                if (code == null) break;
                createStore(TaskTag.RS_OBJECT, new CurrencyContrastDto(CurrencyContrastCache.getCurrency(this.code)));
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
