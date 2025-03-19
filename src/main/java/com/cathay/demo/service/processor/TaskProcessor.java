package com.cathay.demo.service.processor;

import com.cathay.demo.task.FailedTask;
import com.cathay.demo.task.RetryableTask;
import com.cathay.demo.task.StandardTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 為實作 task 的 class 包交易
 */
@Service
@RequiredArgsConstructor
public class TaskProcessor {

    /**
     * 多個任務時，會繼承任務的儲存庫
     * @param tasks 任務 List
     */
    @Transactional
    public void process(List<StandardTask<?>> tasks) {

        // 紀錄前一個 task 用以傳遞 store
        StandardTask<?> previousTask = null;

        // 紀錄若有 RetryableTask ，失敗後要整組重試
        List<RetryableTask<?>> retryableTasks = new ArrayList<>();

        try {
            for (StandardTask<?> task : tasks) {
                // 若遇到 RetryableTask 記錄到 List 以便異常時整組重試
                if (task instanceof RetryableTask) retryableTasks.add((RetryableTask<?>) task);

                // 若有錢個任務，繼承其 storage
                if (previousTask != null) task.inherit(previousTask);
                task.process();
                previousTask = task;
            }
        } catch (RuntimeException e) {
            // 需要回滾時，將需要重試的任務拋給 FailedTaskProcessor 排程
            if (!retryableTasks.isEmpty())
                FailedTaskProcessor.add(new FailedTask(0, 30, retryableTasks));

            // 觸發異常回滾
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void retry(FailedTask task) {
        task.retry();
    }

}
