package com.jerry_mar.mvc.content;

import java.util.LinkedList;
import java.util.List;

public class Observabled implements Observable {
    private List<Observer> observers;
    private boolean atomic;

    public Observabled() {
        observers = new LinkedList<>();
    }

    @Override
    public void bind(Observer o) {
        observers.add(o);
    }

    @Override
    public void unbind(Observer o) {
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
    public void clear() {
        observers.clear();
    }
}
