package com.jerry_mar.mvc;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.jerry_mar.mvc.content.Carrier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jerry_mar.mvc.content.Carrier.CORE_DATA_KEY;

public class DataComponent extends Fragment {
    public static final int RESULT_CORE_DATA = Controller.RESULT_CORE_DATA;

    private Map<String, Object> data = new ArrayMap<>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if(bundle != null || (bundle = getArguments()) != null) {
            populateCoreData(bundle);
        }
        handleBundle(bundle);
    }

    private void populateCoreData(Bundle bundle) {
        ArrayList<String> list = bundle.getStringArrayList(CORE_DATA_KEY);
        if (list != null) {
            Class<?> cls;
            for(int i = 0; i < list.size(); i++) {
                String key = list.get(i);
                String keyName = key.substring(0, key.indexOf('@'));
                String className = key.substring(key.indexOf('@') + 1);
                try{
                    cls = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    cls = null;
                }
                String str = bundle.getString(key);
                bundle.remove(key);
                data.put(keyName, JSON.parseObject(str, cls));
            }
            bundle.remove(CORE_DATA_KEY);
        }
    }

    protected void handleBundle(Bundle bundle) {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<String> keys = new ArrayList<>();
        for (String key : data.keySet()){
            Object coreData = data.get(key);
            key = key + "@" + coreData.getClass().getName();
            String str = JSON.toJSONString(coreData, Carrier.features);
            outState.putString(key, str);
            keys.add(key);
        }
        if(keys.size() > 0) {
            outState.putStringArrayList(CORE_DATA_KEY, keys);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_CORE_DATA && intent != null) {
            Carrier carrier = new Carrier(intent);
            List<String> list = carrier.getStringArrayListExtra(CORE_DATA_KEY);
            if(list != null) {
                Class<?> cls;
                for(int i = 0; i < list.size(); i++) {
                    String key = list.get(i);
                    String keyName = key.substring(0, key.indexOf('@'));
                    String className = key.substring(key.indexOf('@') + 1);
                    try{
                        cls = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        cls = null;
                    }
                    data.put(keyName, carrier.getBean(key, cls));
                    carrier.removeExtra(key);
                }
                carrier.removeExtra(CORE_DATA_KEY);
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public Context getContext() {
        Context context = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context = super.getContext();
        }

        if(context == null) {
            context = getActivity();
        }
        return context;
    }

    public <T> T getCoreData(String key) {
        return (T) data.get(key);
    }

    public void putCoreData(Object value) {
        data.put(value.toString(), value);
    }

    public <T> T removeCoreData(String key) {
        return (T) data.remove(key);
    }

    public static void put(Bundle bundle, Object value) {
        String key = value.toString() + "@" + value.getClass().getName();
        ArrayList<String> keys = bundle.getStringArrayList(CORE_DATA_KEY);
        if (keys == null) {
            keys = new ArrayList<>();
            bundle.putStringArrayList(CORE_DATA_KEY, keys);
        }
        keys.add(key);
        bundle.putString(key, JSON.toJSONString(value, Carrier.features));
    }
}
