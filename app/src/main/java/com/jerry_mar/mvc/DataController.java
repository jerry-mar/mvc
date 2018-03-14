package com.jerry_mar.mvc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.jerry_mar.mvc.content.Carrier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataController extends Activity {
    public static final int RESULT_CORE_DATA = 0xFF;

    private Map<String, Object> data = new ArrayMap<>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Carrier carrier = new Carrier(getIntent());
        populateCoreData(carrier, bundle);
        handleIntent(carrier);
    }

    private void populateCoreData(Carrier carrier, Bundle bundle) {
        ArrayList<String> list;

        if(bundle != null) {
            list = bundle.getStringArrayList(Carrier.CORE_DATA_KEY);
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
                    data.put(keyName, JSON.parseObject(str, cls));
                }
            }
        } else {
            list = carrier.getStringArrayListExtra(Carrier.CORE_DATA_KEY);
            if (list != null)
                complete(list, carrier);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(new Carrier(intent));
    }

    protected void handleIntent(Carrier carrier) {}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ArrayList<String> keys = new ArrayList<>();
        for (String key : data.keySet()){
            Object coreData = data.get(key);
            key = key + "@" + coreData.getClass().getName();
            String str = JSON.toJSONString(coreData, Carrier.features);
            outState.putString(key, str);
            keys.add(key);
        }
        if(keys.size() > 0) {
            outState.putStringArrayList(Carrier.CORE_DATA_KEY, keys);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_CORE_DATA && intent != null) {
            Carrier carrier = new Carrier(intent);
            List<String> list = carrier.getStringArrayListExtra(Carrier.CORE_DATA_KEY);
            if(list != null) {
                complete(list, carrier);
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
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

    private void complete(List<String> list, Carrier carrier) {
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
        carrier.removeExtra(Carrier.CORE_DATA_KEY);
    }
}
