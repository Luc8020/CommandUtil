package com.util.command.core;

import com.util.command.bindings.ArgumentBinding;
import com.util.command.bindings.CommandBinding;
import com.util.command.exceptions.ArgumentNotFoundException;
import com.util.command.exceptions.CommandNotFoundException;
import com.util.command.models.ArgumentModel;

public class CommandService {

    private CommandScanner commandScanner;

    public CommandService(CommandScanner commandScanner) {
        this.commandScanner = commandScanner;
    }

    public ArgumentModel buildArgument(String name, String value){
        ArgumentBinding argumentBinding = getArgument(name);
        ArgumentModel argumentModel = new ArgumentModel();
        argumentModel.setArgumentBinding(argumentBinding);
        argumentModel.setValue(value);
        return argumentModel;
    }

    public CommandBinding getCommand(String name){
        CommandBinding commandBinding = commandScanner.commands.get(name);
        if(commandBinding == null){
            throw new CommandNotFoundException("Command "+ name + " not found");
        }
        return commandBinding;
    }

    public CommandBinding getCommandByPath(String path){
        return commandScanner.commands.get(path);
    }

    public CommandBinding getSubcommand(String parentPath, String subcommandName){
        CommandBinding parentCommand = getCommandByPath(parentPath);
        if (parentCommand == null) {
            throw new CommandNotFoundException("Parent command '" + parentPath + "' not found");
        }

        CommandBinding subcommand = parentCommand.getSubcommands().get(subcommandName);
        if(subcommand == null){
            throw new CommandNotFoundException("Subcommand '" + subcommandName + "' not found for command '" + parentPath + "'");
        }
        return subcommand;
    }

    private ArgumentBinding getArgument(String name){

        ArgumentBinding argumentBinding = commandScanner.arguments.get(name);
        if(argumentBinding == null){
            throw new ArgumentNotFoundException("Argument " + name + " could not be found");
        }

        return argumentBinding;
    }
}
