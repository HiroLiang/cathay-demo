package com.cathay.demo.task;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SuccessTask extends RetryableTask<Void> {
    @Override
    protected void doAction() {
        log.info("process success task");
    }

    @Override
    public Void getData() {
        return null;
    }
}
