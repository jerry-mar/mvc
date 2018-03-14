package com.jerry_mar.mvc.callback;

import android.util.Log;
import android.view.View;

import java.lang.reflect.Method;
import java.util.Map;

public class DefinedOnLongClickListener implements View.OnLongClickListener {
    private Object controller;
    private Method coreMethod;
    private Object target;
    private Method method;
    private boolean init;

    public DefinedOnLongClickListener(Object controller, Object target, Method method) {
        this.controller = controller;
        this.target = target;
        this.method = method;
    }

    @Override
    public boolean onLongClick(View view) {
        Map<String, ?> params;
        try {
            Class<?>[] types = method.getParameterTypes();
            if (types == null || types.length == 0) {
                params = (Map<String, ?>) method.invoke(target);
            } else {
                params = (Map<String, ?>) method.invoke(target, view);
            }
            if (!init) {
                try {
                    if (params == null) {
                        coreMethod = controller.getClass().getMethod(method.getName());
                    } else {
                        coreMethod = controller.getClass().getMethod(method.getName(), Map.class);
                    }
                } catch (Exception e) {
                    coreMethod = null;
                }
                init = true;
            }
            if (coreMethod != null) {
                if (params == null) {
                    params = (Map<String, ?>) coreMethod.invoke(controller);
                } else {
                    params = (Map<String, ?>) coreMethod.invoke(controller, params);
                }
            }
            if (params != null) {
                return true;
            }
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
        }

        return false;
    }
}
