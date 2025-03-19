package com.cathay.demo.model.part;

public interface Storable<V> {

    <T> void createStore(V key, T obj);

    <T> T receive(V key, Class<T> clazz);

    Storable<V> inherit(Storable<V> other);
}
