package com.util.command.bindings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandBinding {
    private Object instance;
    private Method method;
    private List<ArgumentBinding> arguments = new ArrayList<>();
    private Map<String, CommandBinding> subcommands = new HashMap<>();
    private String name;
    private String fullPath;
    private CommandBinding parent;
    private int level;

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

    public Map<String, CommandBinding> getSubcommands() {
        return subcommands;
    }

    public void setSubcommands(Map<String, CommandBinding> subcommands) {
        this.subcommands = subcommands;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public CommandBinding getParent() {
        return parent;
    }

    public void setParent(CommandBinding parent) {
        this.parent = parent;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void addSubcommand(String name, CommandBinding subcommand) {
        subcommand.setParent(this);
        subcommand.setLevel(this.level + 1);
        subcommand.setFullPath(this.fullPath != null ? this.fullPath + "." + name : name);
        this.subcommands.put(name, subcommand);
    }

    public CommandBinding findSubcommand(String[] path, int startIndex) {
        if (startIndex >= path.length) {
            return this;
        }

        String nextCommand = path[startIndex];
        CommandBinding subcommand = subcommands.get(nextCommand);

        if (subcommand == null) {
            return this;
        }

        return subcommand.findSubcommand(path, startIndex + 1);
    }

    public boolean hasSubcommands() {
        return !subcommands.isEmpty();
    }
}