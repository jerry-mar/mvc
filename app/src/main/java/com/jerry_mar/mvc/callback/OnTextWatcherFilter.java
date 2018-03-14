package com.jerry_mar.mvc.callback;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class OnTextWatcherFilter implements Filter {
    @Override
    public void execute(Object scene, Map<Integer, CallbackInfo> target, List<Method> ms) {
        for (int i = 0; i < ms.size(); i++) {
            Method method = ms.get(i);
            if (method.isAnnotationPresent(OnTextWatcher.class)) {
                OnTextWatcher annotation = method.getAnnotation(OnTextWatcher.class);
                int id = annotation.id();
                OnTextWatcher.Type type = annotation.type();
                CallbackInfo info = target.get(id);
                if (info == null) {
                    info = new CallbackInfo();
                    target.put(id, info);
                }
                switch (type) {
                    case beforeTextChanged:
                        info.beforeTextChanged = method;
                        break;
                    case onTextChanged:
                        info.onTextChanged = method;
                        break;
                    case afterTextChanged:
                        info.afterTextChanged = method;
                        break;
                }
                info.target = scene;
                ms.remove(i);
                i--;
            }
        }
    }
}
