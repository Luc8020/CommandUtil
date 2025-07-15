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

    public CommandBinding getCommand(String name){
        CommandBinding commandBinding = commandScanner.commands.get(name);
        if(commandBinding == null){
            throw new CommandNotFoundException("Command "+ name + " not found use help to see a list of all commands");
        }
        return commandBinding;
    }

    public ArgumentModel buildArgument(String name, String value){
        ArgumentBinding argumentBinding = getArgument(name);
        ArgumentModel argumentModel = new ArgumentModel();
        argumentModel.setArgumentBinding(argumentBinding);
        argumentModel.setValue(value);
        return argumentModel;
    }

    private ArgumentBinding getArgument(String name){

        ArgumentBinding argumentBinding = commandScanner.arguments.get(name);
        if(argumentBinding == null){
            throw new ArgumentNotFoundException("Argument " + name + " could not be found");
        }

        return argumentBinding;
    }
}
