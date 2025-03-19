package com.cathay.demo.task;

import lombok.extern.slf4j.Slf4j;

/**
 * 失敗後可重複執行的任務，若是 super 後被中斷，則會丟入 FailedTaskProcessor 等待佇列
 * # 注意 forceFailed 在此物件會導致強制重複執行
 * @param <T> 任務回傳型別
 */
@Slf4j
public abstract class RetryableTask<T> extends StandardTask<T> {

    @Override
    public void process() {
        super.process();
    }

    public void retry() {
        super.process();
    }

}
