package com.util;

import com.util.command.annotations.Argument;
import com.util.command.annotations.Command;
import com.util.command.core.CommandListener;
import com.util.command.core.CommandScanner;

public class Main{
    @Argument(name = "-a", commands = "command")
    public String test;

    @Argument(name = "-b", commands = "command")
    boolean test2;

    public static void main(String[] args) {
        CommandScanner commandScanner = new CommandScanner();
        commandScanner.scan();
        CommandListener.listen(commandScanner);
    }

    @Command(name = "command")
    public void command(){
        if(test2) {
            System.out.println(test);
        }
    }



    @Command
    public void command2(){

    }
}