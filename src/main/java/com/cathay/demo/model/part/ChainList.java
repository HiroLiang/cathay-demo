package com.cathay.demo.model.part;

import java.util.List;
import java.util.function.Supplier;

public interface ChainList<T extends Chainable> {

    ChainList<T> chain(T chainable);

    ChainList<T> chain(T chainable, Supplier<Boolean> condition);

    List<T> build();
}
