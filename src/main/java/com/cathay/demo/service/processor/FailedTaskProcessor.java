package com.cathay.demo.service.processor;

import com.cathay.demo.task.FailedTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 失敗任務 Queue 的重複執行者，有需要可外開 API 啟動, 終止此物件 (start, stop)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FailedTaskProcessor {

    private final DelayQueue<FailedTask> failedTasks = new DelayQueue<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private volatile boolean processing = false;

    /**
     * 實例化後新增線程等待失敗任務
     */
    @PostConstruct
    public void postConstruct() {
        startRetry();
    }

    public void retry() {
        while (processing) {
            try {
                // 阻塞線程等待
                FailedTask task = failedTasks.take();
                try {
                    task.retry();
                } catch (Exception e) {
                    log.warn("Retry Task: {} failed {} times.", task.getClass().getSimpleName(), task.getRetryCount(), e);
                }

                // 若成功執行，下一個
                if (task.isSuccess()) continue;

                // 重試第 3 次
                if (task.getRetryCount() > 2) {
                    alert(task);
                    continue;
                }
                // 若未超過 2 次，延遲 30 分鐘後再次嘗試
                failedTasks.add(task.setDelay(30));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void add(FailedTask task) {
        this.failedTasks.add(task);
    }

    public void stopRetry() {
        if (processing) setProcessing(false);
    }

    public void startRetry() {
        if (processing) return;
        setProcessing(true);
        startThread();
    }

    public boolean isProcessing() {
        lock.readLock().lock();
        try {
            return processing;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void startThread() {
        Thread thread = new Thread(this::retry);
        thread.start();
        log.info("Started listening failed task, thread: {}", thread.getName());
    }

    private void setProcessing(boolean processing) {
        lock.writeLock().lock();
        try {
            this.processing = processing;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void alert(FailedTask task) {
        log.warn("Task: {} failed over 3 times.", task.getClass().getSimpleName());
        // TODO 實作通知管理人員，亦可用連線池異步執行
    }
}
