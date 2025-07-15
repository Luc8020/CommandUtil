package com.util.command.core;

import java.util.Scanner;

public class CommandListener {
    private static boolean running = true;

    public static void listen(CommandScanner commandScanner){
        CommandExecutor commandExecutor = new CommandExecutor(commandScanner);
        Scanner scanner = new Scanner(System.in);

        while(running) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();

            if(!command.isEmpty()){
                if(command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("quit")) {
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                }

                try {
                    commandExecutor.processCommand(command);
                } catch (Exception e) {
                    System.err.println("Error executing command: " + e.getMessage());
                }
            }
        }

        scanner.close();
    }

    public static void stop() {
        running = false;
    }
}