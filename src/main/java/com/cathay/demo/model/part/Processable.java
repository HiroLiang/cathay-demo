package com.cathay.demo.model.part;

/**
 * 可執行的物件
 * @param <T> 定義物件執行後產出
 */
public interface Processable<T> extends Runnable {

    /**
     * 執行方法
     */
    void process();

    /**
     * 回傳處理後的物件
     * @return T
     */
    T getData();
}
