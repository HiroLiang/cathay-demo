package com.cathay.demo.controller;

import com.cathay.demo.model.annotation.LogExecutionTime;
import com.cathay.demo.model.dto.BaseRs;
import com.cathay.demo.model.dto.CurrencyContrastDto;
import com.cathay.demo.service.ServiceCollector;
import com.cathay.demo.service.TaskProcessor;
import com.cathay.demo.task.currency.CurrencyContrastAllData;
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
        processor.process(new CurrencyContrastAllData(serviceCollector).chaining()
                .chain(builder)
                .build());
        return ResponseEntity.ok(builder.getData());
    }

    @GetMapping
    @LogExecutionTime
    @RequestMapping("/get/contrast/{code}")
    public ResponseEntity<BaseRs<CurrencyContrastDto>> getContrast(@PathVariable(name = "code") String code) {
        ResponseBuilder<CurrencyContrastDto> builder = new ResponseBuilder<>();
        processor.process(new CurrencyContrastGetter(serviceCollector).setCode(code).chaining()
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
                .chain(new CurrencyContrastGetter(serviceCollector).setCode(dto.getCode()))
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
                .chain(new CurrencyContrastGetter(serviceCollector).setCode(dto.getCode()))
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
