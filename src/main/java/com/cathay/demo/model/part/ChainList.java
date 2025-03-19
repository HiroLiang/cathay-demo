package com.cathay.demo.model.part;

import java.util.List;
import java.util.function.Supplier;

/**
 * 串連用物件
 * @param <T> 可限定串聯的物件型別
 */
public interface ChainList<T extends Chainable> {

    ChainList<T> chain(T chainable);

    ChainList<T> chain(T chainable, Supplier<Boolean> condition);

    List<T> build();
}
