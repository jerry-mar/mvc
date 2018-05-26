package com.jerry_mar.mvc.task;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Scheduler implements InvocationHandler {
    private Scheduler() {}

    private static String getMethodName(String name) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        int count = elements.length;
        for (int i = 0; i < count; i++) {
            if (elements[i].equals(name) && i < count - 1) {
                return elements[i + 1].getMethodName();
            }
        }
        return null;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}