package com.cathay.demo.util;

import com.cathay.demo.util.adapter.NullStringToEmptyAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConvertUtil {

    public static String toJson(Object obj) {
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
        return gson.toJson(obj);
    }

}
