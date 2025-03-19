package com.cathay.demo.task;

import com.cathay.demo.service.processor.TaskProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 1. 若要確認是否執行要去把 TaskProcessor 加入 FailedTask 的 delay time 改在 5 秒內
 * 2. 此 Test 會 log 錯誤訊息 (不會阻止測試線程)，暫不開啟 (實際運行時會需要從 log 取得意外原因)
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskTest {

    @Autowired
    private TaskProcessor taskProcessor;

//    @Test
    void testRetryableTask() {
        Thread thread = new Thread(() ->
            taskProcessor.process(new SuccessTask().chaining()
                    .chain(new SuccessTask())
                    .chain(new FailTask())
                    .build()));

        try {
            thread.start();
        } catch (Exception ignored) {}

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.error("task process interrupted", e);
        }
    }
}
