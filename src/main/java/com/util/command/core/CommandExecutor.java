package com.util.command.core;

import com.util.command.bindings.ArgumentBinding;
import com.util.command.bindings.CommandBinding;
import com.util.command.exceptions.ArgumentNotFoundException;
import com.util.command.models.ArgumentModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandExecutor {
    private CommandScanner commandScanner;

    public CommandExecutor(CommandScanner commandScanner) {
        this.commandScanner = commandScanner;
    }
    public void processCommand(String input){
        CommandService commandService = new CommandService(commandScanner);

        String[] parts = input.trim().split("\\s+");
        String command = parts[0];
        Map<String, String> arguments = new HashMap<>();

        for (int i = 1; i < parts.length; i++) {
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
        CommandBinding commandBinding = commandService.getCommand(command);
        List<ArgumentModel> argumentModels = new ArrayList<>();
        for (Map.Entry<String, String> entry : arguments.entrySet()){
            argumentModels.add(commandService.buildArgument(entry.getKey(), entry.getValue()));
        }
        executeCommand(commandBinding, argumentModels);
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
                    field.set(instance, value);
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
    }
}
