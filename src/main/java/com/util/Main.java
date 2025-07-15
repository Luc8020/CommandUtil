package com.util;

import com.util.command.annotations.Argument;
import com.util.command.annotations.Command;
import com.util.command.core.CommandScanner;

public class Main {
    @Argument(name = "test", commands = "command")
    public String test;
    public static void main(String[] args) {
        CommandScanner commandScanner = new CommandScanner();
        commandScanner.scan();
        System.out.println("Hello world!");
    }

    @Command(name = "command")
    public void command(){

    }
}