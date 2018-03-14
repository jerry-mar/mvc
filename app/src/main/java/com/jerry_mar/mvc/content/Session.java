package com.jerry_mar.mvc.content;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Session implements Observable, Observer {
    private volatile static Session session;
    private Map<String, Object> params;
    private Map<String, Object> data;
    private List<Observer> observers;
    private boolean atomic;

    Session() {
        params = new HashMap<>();
        data = new HashMap<>();
        observers = new LinkedList<>();
    }

    public static Session get() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    public <T> T get(String name) {
        return (T) params.get(name);
    }

    public <T> T take(String name) {
        return (T) data.get(name);
    }

    public void put(String name, Object target) {
        Object obj = params.get(name);
        if (obj == null || !obj.equals(target)) {
            params.put(name, target);
            if (target instanceof Observable) {
                ((Observable) target).bind(this);
                ((Observable) target).update(this, name, target);
            } else {
                update(name, target);
            }
        }
    }

    public void push(String name, Object target) {
        data.put(name, target);
    }

    public <T> T remove(String name) {
        T result = (T) params.remove(name);
        if (result instanceof Observable) {
            ((Observable) result).unbind(this);
        }
        return result;
    }

    public <T> T pop(String name) {
        return (T) data.remove(name);
    }

    @Override
    public synchronized void bind(Observer o) {
        observers.add(o);
    }

    @Override
    public synchronized void unbind(Observer o) {
        observers.remove(o);
    }

    @Override
    public void update(String name, Object target) {
        Observer[] temp = null;
        synchronized (this) {
            if (!atomic) {
                atomic = true;
                temp = observers.toArray(new Observer[observers.size()]);
                atomic = false;
            }
        }

        if (temp != null) {
            for (int i = temp.length - 1; i >= 0; i--)
                temp[i].urgentNotify(name, target);
        }
    }

    @Override
    public void update(Observer o, String name, Object arg) {
        o.urgentNotify(name, arg);
    }

    @Override
    public synchronized void clear() {
        observers.clear();
    }

    @Override
    public void urgentNotify(String name, Object obj) {
        update(name, obj);
    }

    @Override
    public void urgentNotify(Observer o, String name, Object obj) {
        update(o, name, obj);
    }
}

