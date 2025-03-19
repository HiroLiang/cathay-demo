package com.cathay.demo.task;

import com.cathay.demo.service.ServiceCollector;
import com.cathay.demo.service.processor.FailedTaskProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * 失敗後可重複執行的任務，若是 super 後被中斷，則會丟入 FailedTaskProcessor 等待佇列
 * # 注意 forceFailed 在此物件會導致強制重複執行
 * @param <T> 任務回傳型別
 */
@Slf4j
public abstract class RetryableTask<T> extends StandardTask<T> {

    private final FailedTaskProcessor failedTaskProcessor;

    protected RetryableTask(ServiceCollector collector) {
        this.failedTaskProcessor = collector.getFailedTaskProcessor();
    }

    @Override
    public void process() {
        try {
            super.process();
        } catch (Exception e) {
            log.warn("Process Task: {} failed.", this.getClass().getSimpleName(), e);
            failedTaskProcessor.add(new FailedTask(0, 30, this));
        }
    }

    public void retry() {
        try {
            super.process();
        } catch (Exception e) {
            log.warn("Retry Task: {} failed.", this.getClass().getSimpleName(), e);
        }
    }

}
