package com.cathay.demo.model.part;

public interface Task<T> {

    void process();

    T getData();
}
