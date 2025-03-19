package com.cathay.demo.task.currency;

import com.cathay.demo.model.dto.CurrencyContrastDto;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.service.CurrencyService;
import com.cathay.demo.service.ServiceCollector;
import com.cathay.demo.task.StandardTask;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CurrencyContrastAllData extends StandardTask<List<CurrencyContrastDto>> {

    private final CurrencyService currencyService;

    private final List<CurrencyContrastDto> result = new ArrayList<>();

    public CurrencyContrastAllData(ServiceCollector collector) {
        this.currencyService = collector.getCurrencyService();
    }

    @Override
    protected void doAction() {
        this.result.clear();

        this.result.addAll(currencyService.getAllContrasts().stream()
                .map(CurrencyContrastDto::new)
                .collect(Collectors.toList()));

        createStore(TaskTag.RS_STATUS, RequestStatus.OK);
        createStore(TaskTag.RS_OBJECT, this.result);
    }

    @Override
    public List<CurrencyContrastDto> getData() {
        return result;
    }
}
