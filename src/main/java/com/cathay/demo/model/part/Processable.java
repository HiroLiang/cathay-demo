package com.cathay.demo.model.part;

public interface Processable<T> extends Runnable {

    void process();

    T getData();
}
