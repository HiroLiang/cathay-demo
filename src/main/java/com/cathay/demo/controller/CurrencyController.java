package com.cathay.demo.controller;

import com.cathay.demo.db.entity.CurrencyNameContrast;
import com.cathay.demo.model.annotation.LogExecutionTime;
import com.cathay.demo.model.dto.BaseRs;
import com.cathay.demo.model.dto.CurrencyContrastDto;
import com.cathay.demo.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    @LogExecutionTime
    @RequestMapping("/get-all/contrast")
    public ResponseEntity<List<CurrencyContrastDto>> getAllContrast() {
        List<CurrencyNameContrast> contrasts = currencyService.getAllContrasts();
        return ResponseEntity.ok(contrasts.stream()
                .map(CurrencyContrastDto::new)
                .collect(Collectors.toList()));
    }

    @GetMapping
    @LogExecutionTime
    @RequestMapping("/get/contrast/{code}")
    public ResponseEntity<CurrencyContrastDto> getContrast(@PathVariable(name = "code") String code) {
        return ResponseEntity.ok(new CurrencyContrastDto(currencyService.getContrastByCode(code)));
    }

    @PostMapping
    @LogExecutionTime
    @RequestMapping("/add/contrast")
    public ResponseEntity<CurrencyContrastDto> addContrast(@RequestBody CurrencyContrastDto dto) {
        return ResponseEntity.ok(new CurrencyContrastDto(currencyService.addContrast(dto)));
    }

    @PutMapping
    @LogExecutionTime
    @RequestMapping("/update/contrast")
    public ResponseEntity<CurrencyContrastDto> updateContrast(@RequestBody CurrencyContrastDto dto) {
        return ResponseEntity.ok(new CurrencyContrastDto(currencyService.updateContrast(dto)));
    }

    @DeleteMapping
    @LogExecutionTime
    @RequestMapping("/delete/contrast/{code}")
    public ResponseEntity<Void> deleteContrast(@PathVariable(name = "code") String code) {
        currencyService.deleteContrast(code);
        return ResponseEntity.ok().build();
    }

}
