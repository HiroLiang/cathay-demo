package com.cathay.demo.model.part;

/**
 * 定義儲存空間物件
 * @param <V> 用以判斷儲存物件的 Key (String, enum 等等)
 */
public interface Storable<V> {

    /**
     * 開啟一個儲存
     * @param key 鑰匙 (取出用)
     * @param obj 存入物件
     * @param <T> 存入物件型別
     */
    <T> void createStore(V key, T obj);

    /**
     * 取出儲存物
     * @param key 辨識用鑰匙
     * @param clazz 想轉型的型別 (供自行定義，以防需要轉父類)
     * @return 轉型後物件
     * @param <T> 取出物件型別
     */
    <T> T receive(V key, Class<T> clazz);

    /**
     * 繼承傳入的儲存庫
     * @param other 其他儲存庫
     */
    void inherit(Storable<V> other);
}
