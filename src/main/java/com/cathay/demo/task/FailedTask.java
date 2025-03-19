package com.cathay.demo.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 失敗任務物件，提供
 * 1. 執行時間
 * 2. 執行次數
 * 3. 失敗任務執行狀態確認
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FailedTask implements Delayed {

    private int retryCount = 0;

    private long executeTime;

    private RetryableTask<?> failedTask;

    public FailedTask(RetryableTask<?> failedTask) {
        this.failedTask = failedTask;
        this.executeTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
    }

    public FailedTask setDelay(long delayInMinutes) {
        this.executeTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(delayInMinutes);
        return this;
    }

    public void retry() {
        this.retryCount++;
        this.failedTask.retry();
    }

    public boolean isSuccess() {
        return failedTask.isSuccess();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(executeTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        return Long.compare(this.executeTime, ((FailedTask) other).executeTime);
    }
}
