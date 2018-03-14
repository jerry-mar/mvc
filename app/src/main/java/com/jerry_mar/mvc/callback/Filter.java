package com.jerry_mar.mvc.callback;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public interface Filter {
    void execute(Object scene, Map<Integer, CallbackInfo> target, List<Method> ms);
}
