package com.jerry_mar.mvc.callback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnTextWatcher {
    int id();
    Type type();

    enum Type {
        beforeTextChanged,
        onTextChanged,
        afterTextChanged
    }
}
