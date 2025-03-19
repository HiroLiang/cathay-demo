package com.cathay.demo.controller;

import com.cathay.demo.model.annotation.LogExecutionTime;
import com.cathay.demo.model.dto.CoinDeskDto;
import com.cathay.demo.model.dto.CoinDeskInfoDto;
import com.cathay.demo.service.CoinDeskService;
import com.cathay.demo.service.CurrencyService;
import com.cathay.demo.service.TaskProcessor;
import com.cathay.demo.task.currency.CoinDeskSwitcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coin-desk")
public class CoinDeskController {

    private final CoinDeskService coinDeskService;

    private final CurrencyService currencyService;

    private final TaskProcessor taskProcessor;

    public CoinDeskController(CoinDeskService coinDeskService, CurrencyService currencyService, TaskProcessor taskProcessor) {
        this.coinDeskService = coinDeskService;
        this.currencyService = currencyService;
        this.taskProcessor = taskProcessor;
    }

    @GetMapping
    @LogExecutionTime
    @RequestMapping("/call-api")
    public ResponseEntity<CoinDeskDto> callCoinDesk() {
        return ResponseEntity.ok(coinDeskService.callCoinDesk());
    }

    @GetMapping
    @LogExecutionTime
    @RequestMapping("/get-info")
    public ResponseEntity<CoinDeskInfoDto> getCoinDeskInfo() {
        CoinDeskDto dto = coinDeskService.callCoinDesk();
        CoinDeskSwitcher switchTask = new CoinDeskSwitcher(this.currencyService, dto);
        taskProcessor.process(switchTask.chaining().build());
        return ResponseEntity.ok(switchTask.getData());
    }

}
