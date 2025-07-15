package com.util.command.example;

import com.util.command.annotations.Argument;
import com.util.command.annotations.Command;

public class MultipleCommands {
    @Argument(name = "-m", commands = "print2")
    String message;

    @Argument(name = "-n", commands = "printint")
    int number;

    @Argument(name = "-b")
    boolean present;

    @Command(name = "print2")
    public void print(){
        System.out.println(message);
        if(present){
            System.out.println("-b is present");
        }
    }

    @Command()
    private void printInt(){
        System.out.println(number);
    }
}
