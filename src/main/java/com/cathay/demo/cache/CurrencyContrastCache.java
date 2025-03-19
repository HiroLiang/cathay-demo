package com.cathay.demo.cache;

import com.cathay.demo.db.entity.CurrencyNameContrast;
import com.cathay.demo.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyContrastCache {

    private static final ConcurrentHashMap<String, CurrencyNameContrast> contrastsCache = new ConcurrentHashMap<>();

    private final CurrencyService currencyService;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private volatile boolean processing = false;

    @PostConstruct
    public void init() {
        start();
        log.info("Currency contrast cache initialized");
    }

    public static CurrencyNameContrast getCurrency(String code) {
        return contrastsCache.get(code);
    }

    public static List<CurrencyNameContrast> getAllCurrency() {
        return new ArrayList<>(contrastsCache.values());
    }

    public void start() {
        if (processing) return;
        setProcessing(true);
        scheduler.scheduleAtFixedRate(this::process, 0, 5, TimeUnit.MINUTES);
    }

    public void stop() {
        if (processing) setProcessing(false);
        scheduler.shutdown();
    }

    private void process() {
        if (!processing) {
            scheduler.shutdown();
            return;
        }
        try {
            refreshCache(currencyService.getAllContrasts());
        } catch (Exception e) {
            log.error("Refresh cache failed: {}", String.valueOf(e));
        }
    }

    private synchronized void refreshCache(List<CurrencyNameContrast> contrasts) {
        synchronized (CurrencyContrastCache.contrastsCache) {
            CurrencyContrastCache.contrastsCache.clear();
            contrasts.forEach(contrast ->
                    CurrencyContrastCache.contrastsCache.put(contrast.getCode(), contrast));
        }
        log.info("Refresh cache success: {}", CurrencyContrastCache.contrastsCache);
    }

    private void setProcessing(boolean processing) {
        lock.writeLock().lock();
        try {
            this.processing = processing;
        } finally {
            lock.writeLock().unlock();
        }
    }

}
