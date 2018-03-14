package com.jerry_mar.mvc.callback;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Map;

public class DefaultOnTextChangedListener implements TextWatcher {
    private Object controller;
    private Method beforeMethod;
    private Method onMethod;
    private Method afterMethod;
    private Object target;
    private Method beforeTextChanged;
    private Method onTextChanged;
    private Method afterTextChanged;

    public DefaultOnTextChangedListener(Object controller, Object target,
            Method beforeTextChanged, Method onTextChanged, Method afterTextChanged) {
        this.controller = controller;
        this.target = target;
        this.beforeTextChanged = beforeTextChanged;
        this.onTextChanged = onTextChanged;
        this.afterTextChanged = afterTextChanged;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Map<String, ?> params;
        try {
            if (beforeTextChanged != null) {
                params = (Map<String, ?>) beforeTextChanged.invoke(target, s,
                            start, count, after);
                if (params != null) {
                    try {
                        beforeMethod = controller.getClass().getMethod(beforeTextChanged.getName(),
                                Map.class);
                    } catch (Exception e) {
                        beforeMethod = null;
                    }
                    if (beforeMethod != null) {
                        beforeMethod.invoke(controller, params);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Map<String, ?> params;
        try {
            if (onTextChanged != null) {
                params = (Map<String, ?>) onTextChanged.invoke(target, s,
                        start, before, count);
                if (params != null) {
                    try {
                        onMethod = controller.getClass().getMethod(onTextChanged.getName(),
                                Map.class);
                    } catch (Exception e) {
                        onMethod = null;
                    }
                    if (onMethod != null) {
                        onMethod.invoke(controller, params);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        Map<String, ?> params;
        try {
            if (afterTextChanged != null) {
                params = (Map<String, ?>) afterTextChanged.invoke(target, s);
                if (params != null) {
                    try {
                        afterMethod = controller.getClass().getMethod(afterTextChanged.getName(),
                                Map.class);
                    } catch (Exception e) {
                        afterMethod = null;
                    }
                    if (afterMethod != null) {
                        afterMethod.invoke(controller, params);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
        }
    }
}
