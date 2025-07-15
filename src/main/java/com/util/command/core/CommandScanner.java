package com.util.command.core;

import com.util.command.annotations.Argument;
import com.util.command.annotations.Command;
import com.util.command.annotations.Subcommand;
import com.util.command.bindings.ArgumentBinding;
import com.util.command.bindings.CommandBinding;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class CommandScanner {
    private final Reflections reflections;
    private final InstanceRegistry instanceRegistry = new InstanceRegistry();

    public CommandScanner() {
        this.reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forJavaClassPath())
                        .setScanners(
                                Scanners.MethodsAnnotated,
                                Scanners.FieldsAnnotated
                        )
        );
    }

    Map<String, CommandBinding> commands = new HashMap<>();
    Map<String, ArgumentBinding> arguments = new HashMap<>();


    public void scan(){
        /*
        * Those sets save the methods and fields with the annotations @Command, @Subcommand and @Argument
        */

        Set<Method> commandMethods = reflections.getMethodsAnnotatedWith(Command.class);
        Set<Method> subcommandMethods = reflections.getMethodsAnnotatedWith(Subcommand.class);
        Set<Field> argumentFields = reflections.getFieldsAnnotatedWith(Argument.class);
        Set<Method> unprocessedSubcommands = new HashSet<>(subcommandMethods);

        //This loop saves the @Command annotated Methods in the corresponding map.
        for(Method annotatedMethod : commandMethods){
            try {
                String commandName;

                Command commandAnnotation = annotatedMethod.getAnnotation(Command.class);
                CommandBinding commandBinding = new CommandBinding();
                Object instance = instanceRegistry.getOrCreate(annotatedMethod.getDeclaringClass());

                //When the user doesn't define a name the method name is used as command name
                if (commandAnnotation.name().isEmpty()){
                    commandName = annotatedMethod.getName().toLowerCase();
                } else {
                    commandName = commandAnnotation.name();
                }

                commandBinding.setMethod(annotatedMethod);
                commandBinding.setInstance(instance);
                commandBinding.setName(commandName);
                commandBinding.setFullPath(commandName);
                commandBinding.setLevel(0);

                commands.put(commandName, commandBinding);

                System.out.println("Command: " + commandName);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        int maxAttempts = 10;
        int attempts = 0;

        while (!unprocessedSubcommands.isEmpty() && attempts < maxAttempts) {
            Set<Method> processedInThisPass = new HashSet<>();

            for (Method annotatedMethod : unprocessedSubcommands) {
                try {
                    Subcommand subcommandAnnotation = annotatedMethod.getAnnotation(Subcommand.class);
                    String subcommandName = subcommandAnnotation.name();
                    String parentPath = subcommandAnnotation.parent();

                    CommandBinding parentCommand = findCommandByPath(parentPath);
                    if (parentCommand == null) {
                        continue;
                    }

                    CommandBinding subcommandBinding = new CommandBinding();
                    Object instance = instanceRegistry.getOrCreate(annotatedMethod.getDeclaringClass());

                    subcommandBinding.setMethod(annotatedMethod);
                    subcommandBinding.setInstance(instance);
                    subcommandBinding.setName(subcommandName);

                    parentCommand.addSubcommand(subcommandName, subcommandBinding);

                    commands.put(subcommandBinding.getFullPath(), subcommandBinding);

                    System.out.println("Subcommand: " + subcommandBinding.getFullPath());
                    processedInThisPass.add(annotatedMethod);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            unprocessedSubcommands.removeAll(processedInThisPass);
            attempts++;
        }

        if (!unprocessedSubcommands.isEmpty()) {
            System.err.println("Warning: Some subcommands could not be processed due to missing parents:");
            for (Method method : unprocessedSubcommands) {
                Subcommand sub = method.getAnnotation(Subcommand.class);
                System.err.println("  " + sub.name() + " (parent: " + sub.parent() + ")");
            }
        }

        processArguments(argumentFields);
    }

    private CommandBinding findCommandByPath(String path) {
        return commands.get(path);
    }

    private void processArguments(Set<Field> argumentFields) {
        for (Field annotatedField : argumentFields) {
            try {
                Argument argumentAnnotation = annotatedField.getAnnotation(Argument.class);
                String argumentName = argumentAnnotation.name();
                List<String> targetCommands = new ArrayList<>();

                if (!argumentAnnotation.commands().isEmpty()) {
                    targetCommands.addAll(Arrays.asList(argumentAnnotation.commands().split("\\s*,\\s*")));
                }

                if (!argumentAnnotation.subcommands().isEmpty()) {
                    targetCommands.addAll(Arrays.asList(argumentAnnotation.subcommands().split("\\s*,\\s*")));
                }

                if (targetCommands.isEmpty()) {
                    Method[] methods = annotatedField.getDeclaringClass().getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.getAnnotation(Command.class) != null) {
                            targetCommands.add(method.getName());
                        }
                        if (method.getAnnotation(Subcommand.class) != null) {
                            Subcommand sub = method.getAnnotation(Subcommand.class);
                            String fullPath = sub.parent() + "." + sub.name();
                            targetCommands.add(fullPath);
                        }
                    }
                }

                Object instance = instanceRegistry.getOrCreate(annotatedField.getDeclaringClass());
                ArgumentBinding argumentBinding = new ArgumentBinding();
                argumentBinding.setInstance(instance);
                argumentBinding.setField(annotatedField);
                argumentBinding.setName(argumentName);

                for (String commandPath : targetCommands) {
                    CommandBinding command = commands.get(commandPath);
                    if (command != null) {
                        command.getArguments().add(argumentBinding);
                    } else {
                        System.err.println("No command found for argument: " + commandPath);
                    }
                }

                arguments.put(argumentName, argumentBinding);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
