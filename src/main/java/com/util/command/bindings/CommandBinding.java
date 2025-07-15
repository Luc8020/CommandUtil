package com.util.command.bindings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CommandBinding {
    Object instance;
    Method method;
    List<ArgumentBinding> arguments = new ArrayList<>();

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<ArgumentBinding> getArguments() {
        return arguments;
    }

    public void setArguments(List<ArgumentBinding> arguments) {
        this.arguments = arguments;
    }
}
