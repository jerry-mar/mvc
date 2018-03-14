package com.jerry_mar.mvc.callback;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class OnLongClickFilter implements Filter {
    @Override
    public void execute(Object scene, Map<Integer, CallbackInfo> target, List<Method> ms) {
        for (int i = 0; i < ms.size(); i++) {
            Method method = ms.get(i);
            if (method.isAnnotationPresent(OnLongClick.class)) {
                OnLongClick annotation = method.getAnnotation(OnLongClick.class);
                int id = annotation.value();
                CallbackInfo info = target.get(id);
                if (info == null) {
                    info = new CallbackInfo();
                    target.put(id, info);
                }
                info.onLongClick = method;
                info.target = scene;
                ms.remove(i);
                i--;
            }
        }
    }
}
