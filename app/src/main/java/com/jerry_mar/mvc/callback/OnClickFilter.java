package com.jerry_mar.mvc.callback;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class OnClickFilter implements Filter {
    @Override
    public void execute(Object scene, Map<Integer, CallbackInfo> target, List<Method> ms) {
        for (int i = 0; i < ms.size(); i++) {
            Method method = ms.get(i);
            if (method.getAnnotations().length == 0) {
                ms.remove(i);
                i--;
                continue;
            }
            if (method.isAnnotationPresent(OnClick.class)) {
                OnClick annotation = method.getAnnotation(OnClick.class);
                int id = annotation.value();
                CallbackInfo info = target.get(id);
                if (info == null) {
                    info = new CallbackInfo();
                    target.put(id, info);
                }
                info.onClick = method;
                info.target = scene;
                ms.remove(i);
                i--;
            }
        }
    }
}
