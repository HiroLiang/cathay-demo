package com.cathay.demo.model.part;

/**
 * 可被製成鏈結的物件
 */
public interface Chainable {

    /**
     * 回應串聯用物件
     * @return ChainList
     */
    ChainList<?> chaining();

}
