package com.jerry_mar.mvc.callback;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import java.lang.reflect.Method;

public class OnRefreshCallback implements SwipeRefreshLayout.OnRefreshListener {
    private Method method;
    private Object controller;

    public OnRefreshCallback(Method method, Object controller) {
        this.method = method;
        this.controller = controller;
    }

    @Override
    public void onRefresh() {
        try {
            method.invoke(controller);
        } catch (Exception e) {
            Log.d(controller.getClass().getSimpleName(), e.getMessage());
        }
    }
}
