package com.jerry_mar.mvc.content;

public interface Observer {
    void urgentNotify(String name, Object obj);
    void urgentNotify(Observer o, String name, Object obj);
}
