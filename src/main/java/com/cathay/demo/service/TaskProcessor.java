package com.cathay.demo.service;

import com.cathay.demo.model.part.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskProcessor {

    @Transactional
    public void process(Task<?>... tasks) {
        for (Task<?> task : tasks) {
            task.process();
        }
    }
}
