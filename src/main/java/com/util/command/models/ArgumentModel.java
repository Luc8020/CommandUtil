package com.util.command.models;

import com.util.command.bindings.ArgumentBinding;

public class ArgumentModel {
    ArgumentBinding argumentBinding;
    String value;

    public ArgumentBinding getArgumentBinding() {
        return argumentBinding;
    }

    public void setArgumentBinding(ArgumentBinding argumentBinding) {
        this.argumentBinding = argumentBinding;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
