package com.cathay.demo.task;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FailTask extends RetryableTask<Void> {
    @Override
    protected void doAction() {
        log.info("process fail task");
        throw new RuntimeException("fail");
    }

    @Override
    public Void getData() {
        return null;
    }
}
