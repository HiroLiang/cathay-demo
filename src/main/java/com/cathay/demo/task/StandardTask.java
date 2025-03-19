package com.cathay.demo.task;

import com.cathay.demo.model.assembly.GenericChainList;
import com.cathay.demo.model.enumeration.RequestStatus;
import com.cathay.demo.model.enumeration.TaskTag;
import com.cathay.demo.model.part.ChainList;
import com.cathay.demo.model.part.Chainable;
import com.cathay.demo.model.part.Processable;
import com.cathay.demo.model.part.Storable;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 定義標準可執行任務物件
 * @param <T> 任務回傳物件 (可為 Void ，不是每個任務都會有回傳)
 */
public abstract class StandardTask<T> implements Processable<T>, Storable<TaskTag>, Chainable {

    private final Map<TaskTag, Object> storage = new HashMap<>();

    @Getter
    protected volatile boolean success = false;

    private volatile boolean forceFailed = false;

    private Predicate<StandardTask<?>> condition = null;

    private StandardTask<?> contrastTask = null;

    private volatile boolean asResponse = false;

    @Override
    public void run() {
        this.process();
    }

    @Override
    public void process() {
        // 若有設置 doIf，則會確認 condition 是否滿足
        if (contrastTask != null && condition != null && !condition.test(contrastTask)) return;

        // 執行任務內容 (子類實作)
        doAction();

        // 若未被異常打斷，即算是執行成功，但若任務中有設定 forceFailed ，則會強制 false
        this.success = !forceFailed;
    }

    @Override
    public <V> void createStore(TaskTag key, V obj) {
        this.storage.put(key, obj);
    }

    @Override
    public void inherit(Storable<TaskTag> other) {
        if (other instanceof StandardTask) {
            StandardTask<?> otherTask = (StandardTask<?>) other;
            this.storage.putAll(otherTask.storage);
        }
    }

    @Override
    public <V> V receive(TaskTag key, Class<V> clazz) {
        return clazz.cast(this.storage.get(key));
    }

    @Override
    public ChainList<StandardTask<?>> chaining() {
        return new GenericChainList<StandardTask<?>>().chain(this);
    }

    public StandardTask<T> doIf(StandardTask<?> contrast, Predicate<StandardTask<?>> condition) {
        this.contrastTask = contrast;
        this.condition = condition;
        return this;
    }

    public StandardTask<T> asResponse() {
        this.asResponse = true;
        return this;
    }

    protected <V> void storeResponse(RequestStatus status, V response) {
        if (asResponse) {
            createStore(TaskTag.RS_STATUS, status);
            createStore(TaskTag.RS_OBJECT, response);
        }
    }

    protected void forceFailed() {
        this.forceFailed = true;
    }

    protected abstract void doAction();


}
