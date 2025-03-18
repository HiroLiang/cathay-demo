package com.cathay.demo.service;

import com.cathay.demo.model.part.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 為實作 task 的 class 包交易
 */
@Service
public class TaskProcessor {

    @Transactional
    public void process(Task<?>... tasks) {
        for (Task<?> task : tasks) {
            task.process();
        }
    }
}
