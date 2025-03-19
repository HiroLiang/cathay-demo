package com.cathay.demo.task;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class RetryableTask<T> extends StandardTask<T> {

    @Override
    public void process() {
        try {
            super.process();
        } catch (Exception e) {
            log.warn("Process Task: {} failed.", this.getClass().getSimpleName(), e);

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
