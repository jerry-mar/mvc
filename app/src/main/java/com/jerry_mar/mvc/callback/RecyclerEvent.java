package com.jerry_mar.mvc.callback;

public class RecyclerEvent {
    boolean load;

    void load() {
        load = true;
    }

    public void finish() {
        load = false;
    }
}
