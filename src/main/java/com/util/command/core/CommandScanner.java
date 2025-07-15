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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandScanner {
    private Reflections reflections;

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
                CommandBinding commandBinding = new CommandBinding();
                Object instance = annotatedMethod.getDeclaringClass().getDeclaredConstructor().newInstance();

                commandBinding.setMethod(annotatedMethod);
                commandBinding.setInstance(instance);
                commands.put(annotatedMethod.getAnnotation(Command.class).name(), commandBinding);
                System.out.println(annotatedMethod.getAnnotation(Command.class).name());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        for (Field annotatedField : argumentFields) {
            try {
                Argument argumentAnnotation = annotatedField.getAnnotation(Argument.class);
                String argumentName = argumentAnnotation.name();
                String[] commandNames = argumentAnnotation.commands().split("\\s*,\\s*");

                Object instance = annotatedField.getDeclaringClass().getDeclaredConstructor().newInstance();

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
