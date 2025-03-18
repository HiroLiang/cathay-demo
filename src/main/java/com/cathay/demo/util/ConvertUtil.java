package com.cathay.demo.util;

import com.cathay.demo.util.adapter.NullStringToEmptyAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Convert Object and String
 * 1. adapter 轉換 String null 為空字串
 */
public class ConvertUtil {

    public static Gson gson = new GsonBuilder().
            registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
            .create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

}
