package com.cathay.demo.service.processor;

import com.cathay.demo.task.StandardTask;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 為實作 task 的 class 包交易
 */
@Service
public class TaskProcessor {

    /**
     * 多個任務時，會繼承任務的儲存庫
     * @param tasks 任務 List
     */
    @Transactional
    public void process(List<StandardTask<?>> tasks) {
        StandardTask<?> previousTask = null;
        for (StandardTask<?> task : tasks) {
            if (previousTask != null) task.inherit(previousTask);
            task.process();
            previousTask = task;
        }
    }

}
