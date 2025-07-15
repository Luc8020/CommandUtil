package com.util;


import com.util.command.core.CommandListener;
import com.util.command.core.CommandScanner;

public class Main{

    public static void main(String[] args) {
        CommandScanner commandScanner = new CommandScanner();
        commandScanner.scan();
        CommandListener.listen(commandScanner);
    }
}