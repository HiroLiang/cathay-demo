package com.cathay.demo.controller;

import com.cathay.demo.model.annotation.LogExecutionTime;
import com.cathay.demo.model.dto.BaseRs;
import com.cathay.demo.model.dto.CurrencyContrastDto;
import com.cathay.demo.service.ServiceCollector;
import com.cathay.demo.service.processor.TaskProcessor;
import com.cathay.demo.task.currency.CurrencyContrastAllData;
import com.cathay.demo.task.currency.CurrencyContrastCacheAsRs;
import com.cathay.demo.task.currency.CurrencyContrastGetter;
import com.cathay.demo.task.currency.CurrencyContrastUpdate;
import com.cathay.demo.task.response.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/currency")
public class CurrencyController {

    private final TaskProcessor processor;

    private final ServiceCollector serviceCollector;

    public CurrencyController(TaskProcessor processor, ServiceCollector serviceCollector) {
        this.processor = processor;
        this.serviceCollector = serviceCollector;
    }

    @GetMapping
    @LogExecutionTime
    @RequestMapping("/get-all/contrast")
    public ResponseEntity<BaseRs<List<CurrencyContrastDto>>> getAllContrast() {
        ResponseBuilder<List<CurrencyContrastDto>> builder = new ResponseBuilder<>();
        CurrencyContrastAllData allDataTask = new CurrencyContrastAllData(serviceCollector);
        processor.process(allDataTask.asResponse()
                .chaining()
                // 若是因 DB I/O 異常 (非無資料)，從 Cache 先取得資料
                .chain(new CurrencyContrastCacheAsRs(CurrencyContrastCacheAsRs.Purpose.ALL)
                        .doIf(allDataTask, task -> !task.isSuccess()))
                .chain(builder)
                .build());
        return ResponseEntity.ok(builder.getData());
    }

    @GetMapping
    @LogExecutionTime
    @RequestMapping("/get/contrast/{code}")
    public ResponseEntity<BaseRs<CurrencyContrastDto>> getContrast(@PathVariable(name = "code") String code) {
        ResponseBuilder<CurrencyContrastDto> builder = new ResponseBuilder<>();
        CurrencyContrastGetter getter = new CurrencyContrastGetter(serviceCollector);
        processor.process(getter.setCode(code).asResponse()
                .chaining()
                // 若是因 DB I/O 異常 (非無資料)，從 Cache 先取得資料
                .chain(new CurrencyContrastCacheAsRs(CurrencyContrastCacheAsRs.Purpose.CODE)
                        .setCode(code)
                        .doIf(getter, task -> !task.isSuccess()))
                .chain(builder)
                .build());
        return ResponseEntity.ok(builder.getData());
    }

    @PostMapping
    @LogExecutionTime
    @RequestMapping("/add/contrast")
    public ResponseEntity<BaseRs<CurrencyContrastDto>> addContrast(@RequestBody CurrencyContrastDto dto) {
        ResponseBuilder<CurrencyContrastDto> builder = new ResponseBuilder<>();
        processor.process(new CurrencyContrastUpdate(serviceCollector, CurrencyContrastUpdate.Purpose.INSERT, dto)
                .chaining()
                .chain(new CurrencyContrastGetter(serviceCollector).setCode(dto.getCode()).asResponse())
                .chain(builder)
                .build());
        return ResponseEntity.ok(builder.getData());
    }

    @PutMapping
    @LogExecutionTime
    @RequestMapping("/update/contrast")
    public ResponseEntity<BaseRs<CurrencyContrastDto>> updateContrast(@RequestBody CurrencyContrastDto dto) {
        ResponseBuilder<CurrencyContrastDto> builder = new ResponseBuilder<>();
        processor.process(new CurrencyContrastUpdate(serviceCollector, CurrencyContrastUpdate.Purpose.UPDATE, dto)
                .chaining()
                .chain(new CurrencyContrastGetter(serviceCollector).setCode(dto.getCode()).asResponse())
                .chain(builder)
                .build());
        return ResponseEntity.ok(builder.getData());
    }

    @DeleteMapping
    @LogExecutionTime
    @RequestMapping("/delete/contrast/{code}")
    public ResponseEntity<Void> deleteContrast(@PathVariable(name = "code") String code) {
        processor.process(new CurrencyContrastUpdate(serviceCollector, CurrencyContrastUpdate.Purpose.DELETE,
                new CurrencyContrastDto(code, "", ""))
                .chaining().build());
        return ResponseEntity.ok().build();
    }

}
