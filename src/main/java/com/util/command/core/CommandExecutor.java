package com.util.command.core;

import com.util.command.bindings.ArgumentBinding;
import com.util.command.bindings.CommandBinding;
import com.util.command.exceptions.ArgumentNotFoundException;
import com.util.command.exceptions.CommandNotFoundException;
import com.util.command.models.ArgumentModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CommandExecutor {
    private CommandScanner commandScanner;

    public CommandExecutor(CommandScanner commandScanner) {
        this.commandScanner = commandScanner;
    }
    public void processCommand(String input){
        try {
            CommandService commandService = new CommandService(commandScanner);

            String[] parts = input.trim().split("\\s+");

            CommandBinding commandBinding = null;
            int commandDepth = 0;

            for (int i = 1; i <= parts.length; i++) {
                String[] commandParts = Arrays.copyOfRange(parts, 0, i);
                String potentialPath = String.join(".", commandParts);

                CommandBinding potential = commandService.getCommandByPath(potentialPath);
                if (potential != null) {
                    commandBinding = potential;
                    commandDepth = i;
                } else {
                    break; // No longer command parts
                }
            }

            if (commandBinding == null) {
                commandBinding = commandService.getCommand(parts[0]);
                commandDepth = 1;
            }

            Map<String, String> arguments = new HashMap<>();
            for (int i = commandDepth; i < parts.length; i++) {
                if (parts[i].startsWith("-")) {
                    String flag = parts[i];
                    StringBuilder valueBuilder = new StringBuilder();

                    for (int j = i + 1; j < parts.length; j++) {
                        if (parts[j].startsWith("-")) {
                            break;
                        }
                        if (!valueBuilder.isEmpty()) {
                            valueBuilder.append(" ");
                        }
                        valueBuilder.append(parts[j]);
                        i = j;
                    }

                    arguments.put(flag, valueBuilder.toString());
                }
            }

            List<ArgumentModel> argumentModels = new ArrayList<>();
            for (Map.Entry<String, String> entry : arguments.entrySet()){
                argumentModels.add(commandService.buildArgument(entry.getKey(), entry.getValue()));
            }

            executeCommand(commandBinding, argumentModels);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process command: " + input, e);
        }
    }
    public void executeCommand(CommandBinding command, List<ArgumentModel> arguments){
        for(ArgumentModel argument : arguments){
            try {
                if(!command.getArguments().contains(argument.getArgumentBinding())){
                    throw new ArgumentNotFoundException("Argument " + argument.getArgumentBinding().getName() + "can't be used in this command");
                }
                Object instance = argument.getArgumentBinding().getInstance();
                Field field = argument.getArgumentBinding().getField();
                String value = argument.getValue();

                field.setAccessible(true);
                if(field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)){
                    field.set(instance, true);
                }else {
                    field.set(instance, convertStringToType(value, field.getType()));
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set field value", e);
            } catch (Exception e) {
                throw new RuntimeException("Error processing argument", e);
            }
        }

        Object instance = command.getInstance();
        Method method = command.getMethod();
        method.setAccessible(true);

        try {
            method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        for(ArgumentModel argument : arguments){
            try {
                Object argumentInstance = argument.getArgumentBinding().getInstance();
                Field field = argument.getArgumentBinding().getField();

                field.setAccessible(true);
                field.set(argumentInstance, getDefaultValue(field.getType()));

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to set field value", e);
            } catch (Exception e) {
                throw new RuntimeException("Error processing argument", e);
            }
        }
    }

    public static Object getDefaultValue(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == boolean.class) return false;
            if (clazz == char.class) return '\0';
            if (clazz == byte.class) return (byte) 0;
            if (clazz == short.class) return (short) 0;
            if (clazz == int.class) return 0;
            if (clazz == long.class) return 0L;
            if (clazz == float.class) return 0f;
            if (clazz == double.class) return 0d;
        }
        return null;
    }

    public static Object convertStringToType(String value, Class<?> targetType) {
        if (value == null) {
            return targetType.isPrimitive() ? getDefaultValue(targetType) : null;
        }

        if (targetType == String.class) return value;
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(value);
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(value);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(value);
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(value);
        if (targetType == float.class || targetType == Float.class) return Float.parseFloat(value);
        if (targetType == short.class || targetType == Short.class) return Short.parseShort(value);
        if (targetType == byte.class || targetType == Byte.class) return Byte.parseByte(value);
        if (targetType == char.class || targetType == Character.class) {
            if (value.length() != 1) throw new IllegalArgumentException("Invalid char: " + value);
            return value.charAt(0);
        }

        try {
            Constructor<?> ctor = targetType.getConstructor(String.class);
            return ctor.newInstance(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot convert String to " + targetType.getName(), e);
        }
    }


}
