package com.jerry_mar.mvc.content;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Storage {
    private static Context context;
    private static Map<String, SharedPreferences> preferences;
    private static Map<String, SharedPreferences.Editor> editors;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    public static Storage getStorage(Context context, String name) {
        if (Storage.context == null) {
            synchronized (Storage.class) {
                if (Storage.context == null) {
                    Storage.context = context.getApplicationContext();
                    Storage.preferences = new HashMap<>();
                    Storage.editors = new HashMap<>();
                }
            }
        }

        return new Storage(name);
    }

    private Storage(String name) {
        shared = preferences.get(name);
        editor = editors.get(name);
        if (shared == null || editor == null) {
            synchronized (Storage.class) {
                shared = preferences.get(name);
                editor = editors.get(name);
                if (shared == null || editor == null) {
                    shared = context.getSharedPreferences(name, Context.MODE_PRIVATE);
                    editor = shared.edit();
                    preferences.put(name, shared);
                    editors.put(name, editor);
                }
            }
        }
    }

    public String getString(String name) {
        return getString(name, null);
    }

    public String getString(String name, String defValue) {
        return shared.getString(name, defValue);
    }

    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public boolean getBoolean(String name, boolean defValue) {
        return shared.getBoolean(name, defValue);
    }

    public int getInt(String name) {
        return getInt(name, 0);
    }

    public int getInt(String name, int defValue) {
        return shared.getInt(name, defValue);
    }

    public float getFloat(String name) {
        return getFloat(name, 0);
    }

    public float getFloat(String name, float defValue) {
        return shared.getFloat(name, defValue);
    }

    public long getLong(String name) {
        return getLong(name, 0);
    }

    public long getLong(String name, long defValue) {
        return shared.getLong(name, defValue);
    }

    public Set<String> getStringSet(String name) {
        return getStringSet(name, null);
    }

    public Set<String> getStringSet(String name, Set<String> defValue) {
        return shared.getStringSet(name, defValue);
    }

    public Map<String, ?> getAll() {
        return shared.getAll();
    }

    public Storage remove(String name) {
        editor.remove(name);
        return this;
    }

    public Storage clear() {
        editor.clear();
        return this;
    }

    public Storage putString(String name, String value) {
        editor.putString(name, value);
        return this;
    }

    public Storage putBoolean(String name, boolean value) {
        editor.putBoolean(name, value);
        return this;
    }

    public Storage putInt(String name, int value) {
        editor.putInt(name, value);
        return this;
    }

    public Storage putFloat(String name, float value) {
        editor.putFloat(name, value);
        return this;
    }

    public Storage putLong(String name, long value) {
        editor.putLong(name, value);
        return this;
    }

    public Storage putStringSet(String name, Set<String> value) {
        editor.putStringSet(name, value);
        return this;
    }

    public Storage apply() {
        editor.apply();
        return this;
    }

    public Storage commit() {
        editor.commit();
        return this;
    }
}
