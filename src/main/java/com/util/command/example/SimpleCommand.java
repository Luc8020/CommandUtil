package com.util.command.example;

import com.util.command.annotations.Argument;
import com.util.command.annotations.Command;

public class SimpleCommand {
    @Argument(name = "-m")
    String message;

    @Argument(name = "-b")
    boolean present;

    @Command()
    public void print(){
        System.out.println(message);
        if(present){
            System.out.println("-b is present");
        }
    }
}
