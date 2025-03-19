package com.cathay.demo.task;

import com.cathay.demo.model.assembly.GenericChainList;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.model.part.ChainList;
import com.cathay.demo.model.part.Chainable;
import com.cathay.demo.model.part.Processable;
import com.cathay.demo.model.part.Storable;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public abstract class StandardTask<T> implements Processable<T>, Storable<TaskTag>, Chainable {

    private final Map<TaskTag, Object> storage = new HashMap<>();

    @Getter
    protected boolean success = false;

    @Override
    public void run() {
        this.process();
    }

    @Override
    public void process() {
        doAction();
        this.success = true;
    }

    @Override
    public <V> void createStore(TaskTag key, V obj) {
        this.storage.put(key, obj);
    }

    @Override
    public Storable<TaskTag> inherit(Storable<TaskTag> other) {
        if (other instanceof StandardTask) {
            StandardTask<?> otherTask = (StandardTask<?>) other;
            this.storage.putAll(otherTask.storage);
        }
        return this;
    }

    @Override
    public <V> V receive(TaskTag key, Class<V> clazz) {
        return clazz.cast(this.storage.get(key));
    }

    @Override
    public ChainList<StandardTask<?>> chaining() {
        return new GenericChainList<StandardTask<?>>().chain(this);
    }

    protected abstract void doAction();
}
