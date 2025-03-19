package com.cathay.demo.controller;

import com.cathay.demo.model.annotation.LogExecutionTime;
import com.cathay.demo.model.dto.BaseRs;
import com.cathay.demo.model.dto.CoinDeskDto;
import com.cathay.demo.model.dto.CoinDeskInfoDto;
import com.cathay.demo.service.ServiceCollector;
import com.cathay.demo.service.processor.TaskProcessor;
import com.cathay.demo.task.currency.CoinDeskCaller;
import com.cathay.demo.task.currency.CoinDeskSwitcher;
import com.cathay.demo.task.currency.CurrencyContrastAllData;
import com.cathay.demo.task.currency.CurrencyContrastCacheAsData;
import com.cathay.demo.task.response.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coin-desk")
public class CoinDeskController {

    private final ServiceCollector serviceCollector;

    private final TaskProcessor processor;

    public CoinDeskController(ServiceCollector serviceCollector, TaskProcessor taskProcessor) {
        this.serviceCollector = serviceCollector;
        this.processor = taskProcessor;
    }

    @GetMapping
    @LogExecutionTime
    @RequestMapping("/call-api")
    public ResponseEntity<BaseRs<CoinDeskDto>> callCoinDesk() {
        ResponseBuilder<CoinDeskDto> builder = new ResponseBuilder<>();
        processor.process(new CoinDeskCaller(serviceCollector).asResponse().chaining()
                .chain(builder)
                .build());
        return ResponseEntity.ok(builder.getData());
    }

    @GetMapping
    @LogExecutionTime
    @RequestMapping("/get-info")
    public ResponseEntity<BaseRs<CoinDeskInfoDto>> getCoinDeskInfo() {
        ResponseBuilder<CoinDeskInfoDto> builder = new ResponseBuilder<>();
        CurrencyContrastAllData allContrastTask = new CurrencyContrastAllData(serviceCollector);
        processor.process(new CoinDeskCaller(serviceCollector).chaining()
                .chain(allContrastTask)
                // 若是因 DB I/O 異常 (非無資料)，從 Cache 先取得資料
                .chain(new CurrencyContrastCacheAsData(CurrencyContrastCacheAsData.Purpose.ALL)
                        .doIf(allContrastTask, task -> !task.isSuccess()))
                .chain(new CoinDeskSwitcher().asResponse())
                .chain(builder)
                .build());
        return ResponseEntity.ok(builder.getData());
    }

}
