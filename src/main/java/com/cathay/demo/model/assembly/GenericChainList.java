package com.cathay.demo.model.assembly;

import com.cathay.demo.model.part.ChainList;
import com.cathay.demo.model.part.Chainable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 標準 Chain List，簡單使用 Array List
 * @param <T> 使用時指定要 Chain 的型別
 */
public class GenericChainList<T extends Chainable> implements ChainList<T> {

    protected final List<T> list = new ArrayList<>();

    @Override
    public ChainList<T> chain(T chainable) {
        this.list.add(chainable);
        return this;
    }

    @Override
    public ChainList<T> chain(T chainable, Supplier<Boolean> condition) {
        if (condition.get()) this.list.add(chainable);

        return this;
    }

    @Override
    public List<T> build() {
        return this.list;
    }
}
