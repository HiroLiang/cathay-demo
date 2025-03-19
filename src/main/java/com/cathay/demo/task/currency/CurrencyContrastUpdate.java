package com.cathay.demo.task.currency;

import com.cathay.demo.model.dto.CurrencyContrastDto;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.exception.GenericException;
import com.cathay.demo.service.currency.CurrencyService;
import com.cathay.demo.service.ServiceCollector;
import com.cathay.demo.task.RetryableTask;

public class CurrencyContrastUpdate extends RetryableTask<Void> {

    private final CurrencyService currencyService;

    private final Purpose purpose;

    private final CurrencyContrastDto contrast;

    public CurrencyContrastUpdate(ServiceCollector collector, Purpose purpose, CurrencyContrastDto contrast) {
        super(collector);
        this.currencyService = collector.getCurrencyService();
        this.purpose = purpose;
        this.contrast = contrast;
    }

    @Override
    protected void doAction() {
        if (this.contrast == null) throw new GenericException(RequestStatus.DB_FAIL, "Update contract not found");

        switch (purpose) {
            case INSERT:
                currencyService.addContrast(this.contrast);
                break;
            case UPDATE:
                currencyService.updateContrast(this.contrast);
                break;
            case DELETE:
                currencyService.deleteContrast(this.contrast.getCode());
                break;
        }
    }

    @Override
    public Void getData() {
        return null;
    }

    public enum Purpose {
        INSERT,
        UPDATE,
        DELETE
    }
}
