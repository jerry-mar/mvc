package com.jerry_mar.mvc.callback;

import java.lang.reflect.Method;

public class CallbackInfo {
    public Object target;
    public Method onClick;
    public Method onLongClick;
    public Method beforeTextChanged;
    public Method onTextChanged;
    public Method afterTextChanged;
}
