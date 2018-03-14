package com.jerry_mar.mvc.content;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.ArrayList;

public class Carrier extends Intent {
    public static final String CORE_DATA_KEY = "CORE_DATA_KEY";
    public static final SerializerFeature[] features = new SerializerFeature[]{
            SerializerFeature.QuoteFieldNames,
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.SkipTransientField,
            SerializerFeature.WriteClassName,
            SerializerFeature.DisableCircularReferenceDetect,
            SerializerFeature.IgnoreErrorGetter,
            SerializerFeature.IgnoreNonFieldGetter
    };


    public Carrier() {
        super();
    }

    public Carrier(Intent intent) {
        super(intent);
    }

    public Carrier(String action) {
        super(action);
    }

    public Carrier(String action, Uri uri) {
        super(action, uri);
    }

    public Carrier(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public Carrier(String action, Uri uri, Context packageContext, Class<?> cls) {
        super(action, uri,packageContext,cls);
    }

    public Carrier putExtra(Object value) {
        return putExtra(wrap(value), value);
    }

    public Carrier putExtra(String key, Object value) {
        String str = JSON.toJSONString(value, features);
        super.putExtra(key, str);
        return this;
    }

    public <T> T getBean(String name, Class<? extends T> cls) {
        String str = getStringExtra(name);
        return JSON.parseObject(str, cls);
    }

    protected String wrap(Object value) {
        String key = value.toString() + "@" + value.getClass().getName();
        ArrayList<String> keys = getStringArrayListExtra(CORE_DATA_KEY);
        if (keys == null) {
            keys = new ArrayList<>();
            putStringArrayListExtra(CORE_DATA_KEY, keys);
        }
        keys.add(key);
        return key;
    }
}
