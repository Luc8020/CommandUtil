package com.util.command.core;

import java.util.Scanner;

public class CommandListener {
    public static void listen(CommandScanner commandScanner){
        CommandExecutor commandExecutor = new CommandExecutor(commandScanner);
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        if(!command.isEmpty()){
            commandExecutor.processCommand(command);
        }
    }
}
