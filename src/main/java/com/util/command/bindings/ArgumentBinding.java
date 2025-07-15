package com.util.command.bindings;

import java.lang.reflect.Field;

public class ArgumentBinding {
    Object instance;
    Field field;
    String name;

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
