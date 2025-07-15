package com.util.command.core;

import com.util.command.annotations.Argument;
import com.util.command.annotations.Command;
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
    private Reflections reflections;
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

        Set<Method> commandMethods = reflections.getMethodsAnnotatedWith(Command.class);
        Set<Field> argumentFields = reflections.getFieldsAnnotatedWith(Argument.class);

        for(Method annotatedMethod : commandMethods){
            try {
                String commandName;
                Command commandAnnotation = annotatedMethod.getAnnotation(Command.class);
                CommandBinding commandBinding = new CommandBinding();
                Object instance = instanceRegistry.getOrCreate(annotatedMethod.getDeclaringClass());

                if (commandAnnotation.name().isEmpty()){
                    commandName = annotatedMethod.getName().toLowerCase();
                } else {
                    commandName = commandAnnotation.name();
                }

                commandBinding.setMethod(annotatedMethod);
                commandBinding.setInstance(instance);
                commands.put(commandName, commandBinding);
                System.out.println(commandName);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        for (Field annotatedField : argumentFields) {
            try {
                Argument argumentAnnotation = annotatedField.getAnnotation(Argument.class);
                String argumentName = argumentAnnotation.name();
                List<String> commandNames = new ArrayList<>(Arrays.stream(argumentAnnotation.commands().split("\\s*,\\s*")).toList());
                if(argumentAnnotation.commands().isEmpty()){
                    Method[] methods = annotatedField.getDeclaringClass().getDeclaredMethods();
                    for(Method method : methods){
                        if(method.getAnnotation(Command.class) != null){
                            commandNames.add(method.getName());
                        }
                    }
                }

                Object instance = instanceRegistry.getOrCreate(annotatedField.getDeclaringClass());

                ArgumentBinding argumentBinding = new ArgumentBinding();
                argumentBinding.setInstance(instance);
                argumentBinding.setField(annotatedField);
                argumentBinding.setName(argumentName);

                for (String commandName : commandNames) {
                    if (commands.get(commandName) != null) {
                        commands.get(commandName).getArguments().add(argumentBinding);
                    } else {
                        System.err.println("No command found for argument: " + commandName);
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
